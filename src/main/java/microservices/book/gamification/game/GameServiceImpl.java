package microservices.book.gamification.game;

import jakarta.transaction.Transactional;
import microservices.book.gamification.challenge.ChallengeSolvedEvent;
import microservices.book.gamification.game.badgeprocessors.BadgeProcessor;
import microservices.book.gamification.game.domain.BadgeCard;
import microservices.book.gamification.game.domain.BadgeType;
import microservices.book.gamification.game.domain.ScoreCard;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final ScoreRepository scoreRepository;

    private final BadgeRepository badgeRepository;

    private final List<BadgeProcessor> badgeProcessors;

    @Transactional
    @Override
    public GameResult newAttemptForUser(final ChallengeSolvedEvent challenge) {
        GameResult result;
        // We give points only if it's correct
        if (challenge.isCorrect()) {
            ScoreCard scoreCard = new ScoreCard(challenge.getUserId(), challenge.getAttemptId());
            scoreRepository.save(scoreCard);
            log.info("User {} scored {} points for attempt id {}",
                    challenge.getUserAlias(), scoreCard.getScore(), challenge.getAttemptId());

            List<BadgeCard> badgeCards = processForBadges(challenge);
            result = new GameResult(
                    scoreCard.getScore(),
                    badgeCards.stream()
                            .map(BadgeCard::getBadgeType)
                            .collect(Collectors.toList())
            );
        } else {
            log.info("Attempt id {} is not correct. " + "User {} does not get score.",
                    challenge.getAttemptId(), challenge.getUserId());
            result = new GameResult(0, List.of());
        }
        return result;
    }

    /**
     * Check the total score and the different scorecards obtained
     * to give new badges in case their conditions are met.
     */
    private List<BadgeCard> processForBadges(final ChallengeSolvedEvent solvedChallenge) {
        Optional<Integer> optTotalScore = scoreRepository.getTotalScoreForUser(solvedChallenge.getUserId());
        if (optTotalScore.isEmpty()) {
            return Collections.emptyList();
        }
        int totalScore = optTotalScore.get();
        // Get the total score and existing badges for the user
        List<ScoreCard> scoreCardList = scoreRepository.findByUserIdOrderByScoreTimestampDesc(solvedChallenge.getUserId());
        Set<BadgeType> alreadyGotBadges = badgeRepository
                .findByUserIdOrderByBadgeTimestampDesc(solvedChallenge.getUserId())
                .stream()
                .map(BadgeCard::getBadgeType)
                .collect(Collectors.toSet());

        // Calls the badge processors for badges that the user doesn't have yet
        List<BadgeCard> newBadgeCards = badgeProcessors
                .stream()
                .filter(bp -> !alreadyGotBadges.contains(bp.badgeType()))
                .map(bp -> bp.processForOptionalBadge(totalScore, scoreCardList, solvedChallenge))
                .flatMap(Optional::stream)
                .map(badgeType -> new BadgeCard(solvedChallenge.getUserId(), badgeType))
                .collect(Collectors.toList());
        badgeRepository.saveAll(newBadgeCards);
        return newBadgeCards;
    }
}
