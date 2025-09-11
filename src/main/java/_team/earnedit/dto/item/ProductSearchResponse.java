package _team.earnedit.dto.item;

import _team.earnedit.dto.item.NaverProductResponse.NaverProductItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResponse {
    
    @Schema(description = "검색 정보")
    private SearchInfo searchInfo;
    
    @Schema(description = "상품 목록")
    private List<ProductItem> products;
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchInfo {
        
        @Schema(description = "전체 검색 결과 수")
        private Integer totalCount;
        
        @Schema(description = "검색어")
        private String query;
        
        @Schema(description = "캐시 사용 여부")
        private Boolean useCache;
        
        @Schema(description = "배경 제거 여부")
        private Boolean removeBackground;
        
        @Schema(description = "검색 아이템 개수")
        private Integer display;
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem {
        
        @Schema(description = "상품 ID")
        private String id;
        
        @Schema(description = "상품명")
        private String name;
        
        @Schema(description = "가격")
        private Double price;
        
        @Schema(description = "이미지 URL")
        private String imageUrl;
        
        @Schema(description = "상품 링크")
        private String url;
        
        @Schema(description = "제조사")
        private String maker;
        
        public static ProductItem from(NaverProductItem item, String processedImageUrl) {
            return ProductItem.builder()
                    .id(item.getProductId())
                    .name(removeHtmlTags(item.getTitle()))
                    .price(parsePrice(item.getLprice()))
                    .imageUrl(processedImageUrl)
                    .url(item.getLink())
                    .maker(item.getMaker()) // 네이버 API의 maker 필드 사용
                    .build();
        }
        
        private static String removeHtmlTags(String text) {
            if (text == null) return null;
            return text.replaceAll("<[^>]*>", "");
        }
        
        private static Double parsePrice(String priceStr) {
            if (priceStr == null || priceStr.isEmpty()) return 0.0;
            try {
                return Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }
} 