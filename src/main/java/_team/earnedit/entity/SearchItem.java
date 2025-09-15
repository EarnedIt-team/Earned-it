package _team.earnedit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "search_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String productId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String maker;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Column(length = 500)
    private String productUrl;
} 