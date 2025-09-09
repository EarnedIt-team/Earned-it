package _team.earnedit.dto.item;

import _team.earnedit.entity.Item;
import _team.earnedit.entity.Rarity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    @Schema(description = "상품 ID", example = "1")
    private Long id;
    
    @Schema(description = "상품명", example = "청바지")
    private String name;
    
    @Schema(description = "브랜드명", example = "리바이스")
    private String vendor;
    
    @Schema(description = "가격", example = "89000")
    private Long price;
    
    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    private String image;
    
    @Schema(description = "상품 설명", example = "편안한 청바지입니다")
    private String description;
    
    @Schema(description = "희귀도", example = "A")
    private Rarity rarity;
    
    @Schema(description = "카테고리", example = "의류")
    private String category;
    
    public static ProductResponse from(Item item) {
        return ProductResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .vendor(item.getVendor())
                .price(item.getPrice())
                .image(item.getImage())
                .description(item.getDescription())
                .rarity(item.getRarity())
                .category(item.getCategory())
                .build();
    }
} 