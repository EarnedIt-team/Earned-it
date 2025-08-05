package _team.earnedit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long price;

    @Column(nullable = false)
    private String itemImage;

    @Column(nullable = false)
    @Builder.Default
    private boolean isBought = false;

    private String vendor;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean isStarred = false;

    private String url;


    public void update(String name, int price, String itemImage, String vendor, String url) {
        this.name = name;
        this.price = price;
        this.itemImage = itemImage;
        this.vendor = vendor;
        this.url = url;
    }

    // Star 상태
    public void setStarred(boolean starred) {
        this.isStarred =  starred;
    }

    // Bought 상태
    public void setBought(boolean bought) {
        this.isBought =  bought;
    }

}
