package _team.earnedit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Piece {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private boolean isCollected;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime collectedAt;

    @Column(name ="is_main", nullable = false)
    @Builder.Default
    private boolean isMain = false;

    public void markAsMain() {
        this.isMain = true;
    }

    public void unmarkAsMain() {
        this.isMain = false;
    }

}
