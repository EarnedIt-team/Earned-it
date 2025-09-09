package _team.earnedit.dto.item;

import _team.earnedit.entity.Rarity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCondition {
    
    @Schema(description = "검색 키워드 (상품명 또는 브랜드명)", example = "청바지")
    private String keyword;
    
    @Schema(description = "카테고리", example = "의류")
    private String category;
    
    @Schema(description = "희귀도", example = "A")
    private Rarity rarity;
    
    @Schema(description = "최소 가격", example = "10000")
    private Long minPrice;
    
    @Schema(description = "최대 가격", example = "100000")
    private Long maxPrice;
} 