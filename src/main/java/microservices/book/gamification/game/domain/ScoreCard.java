package microservices.book.gamification.game.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ScoreCard {

    public static final int DEFAULT_SCORE = 10;

    @Id
    @GeneratedValue
    private Long cardId;

    private Long userId;

    private Long attemptId;

    @EqualsAndHashCode.Exclude
    private long scoreTimestamp;

    private int score;

    public ScoreCard(final Long userId, final Long attemptId) {
        this(null, userId, attemptId, System.currentTimeMillis(), DEFAULT_SCORE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ScoreCard scoreCard = (ScoreCard) o;
        return cardId != null && Objects.equals(cardId, scoreCard.cardId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}



