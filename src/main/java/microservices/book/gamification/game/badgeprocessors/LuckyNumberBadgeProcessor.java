package microservices.book.gamification.game.badgeprocessors;

import microservices.book.gamification.challenge.ChallengeSolvedDTO;
import microservices.book.gamification.game.domain.BadgeType;
import microservices.book.gamification.game.domain.ScoreCard;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LuckyNumberBadgeProcessor implements BadgeProcessor{

    private static final int LUCKY_NUMBER = 42;

    @Override
    public Optional<BadgeType> processForOptionalBadge(
            int currentScore, List<ScoreCard> scoreCardList, ChallengeSolvedDTO solvedDTO
    ) {
        return (solvedDTO.getFactorA() == LUCKY_NUMBER) || (solvedDTO.getFactorB() == LUCKY_NUMBER)
                ? Optional.of(BadgeType.LUCKY_NUMBER) : Optional.empty();
    }

    @Override
    public BadgeType badgeType() {
        return BadgeType.LUCKY_NUMBER;
    }
}
