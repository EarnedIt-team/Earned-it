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

    // 네이버 상품 정보 추가 필드들
    @Column(name = "product_id")
    private String productId; // 네이버 상품 ID
    
    @Column(name = "product_url")
    private String url; // 상품 링크
    
    private String maker; // 제조사
    
    @Column(name = "product_type")
    private String productType; // 상품 타입
    
    @Column(name = "category2")
    private String category2; // 세부 카테고리
    
    @Column(name = "category3") 
    private String category3; // 더 세부 카테고리
    
    @Column(name = "category4")
    private String category4; // 가장 세부 카테고리

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
    
    // 네이버 상품 정보 업데이트 메서드
    public void updateNaverProductInfo(String productId, String url, String maker, 
                                      String productType, String category2, 
                                      String category3, String category4) {
        this.productId = productId;
        this.url = url;
        this.maker = maker;
        this.productType = productType;
        this.category2 = category2;
        this.category3 = category3;
        this.category4 = category4;
    }
}
