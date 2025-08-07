package _team.earnedit.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String vendor;

    @Column(nullable = false)
    private long price;

    @Column(nullable = false)
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rarity rarity;

    private String category;

    @Column(nullable = false)
    private String description;

    public void update(String name, String vendor, long price, String image,
                       String description, Rarity rarity, String category) {
        this.name = name;
        this.vendor = vendor;
        this.price = price;
        this.image = image;
        this.description = description;
        this.rarity = rarity;
        this.category = category;
    }
}
