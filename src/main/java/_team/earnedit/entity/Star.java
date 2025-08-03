package _team.earnedit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.boot.model.naming.Identifier;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Star {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wish_id", nullable = false)
    private Wish wish;

    @Setter
    private int rank;

    public void updateRank(int rank) {
        this.rank = rank;
    }
}
