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
public class BadgeCard {

    @Id
    @GeneratedValue
    private Long badgeId;


    private Long userId;

    @EqualsAndHashCode.Exclude
    private long badgeTimestamp;

    private BadgeType badgeType;

    public BadgeCard(final Long userId, final BadgeType badgeType) {
        this(null, userId, System.currentTimeMillis(), badgeType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BadgeCard badgeCard = (BadgeCard) o;
        return badgeId != null && Objects.equals(badgeId, badgeCard.badgeId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
