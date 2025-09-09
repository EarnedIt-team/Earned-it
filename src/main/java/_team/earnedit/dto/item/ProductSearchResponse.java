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
    
    @Schema(description = "상품 목록")
    private List<ProductItem> products;
    
    @Schema(description = "전체 검색 결과 수")
    private Integer totalCount;
    
    @Schema(description = "검색어")
    private String query;
    
    @Schema(description = "캐시 사용 여부")
    private Boolean useCache;
    
    @Schema(description = "배경 제거 여부")
    private Boolean removeBackground;
    
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
        
        @Schema(description = "쇼핑몰명")
        private String mallName;
        
        @Schema(description = "상품 타입")
        private String productType;
        
        @Schema(description = "제조사")
        private String maker;
        
        @Schema(description = "카테고리")
        private List<String> categories;
        
        public static ProductItem from(NaverProductItem item, String processedImageUrl) {
            return ProductItem.builder()
                    .id(item.getProductId())
                    .name(removeHtmlTags(item.getTitle()))
                    .price(parsePrice(item.getLprice()))
                    .imageUrl(processedImageUrl)
                    .url(item.getLink())
                    .mallName(item.getMallName())
                    .productType(item.getProductType())
                    .maker(item.getMaker())
                    .categories(extractCategories(item))
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
        
        private static List<String> extractCategories(NaverProductItem item) {
            return List.of(item.getCategory1(), item.getCategory2(), item.getCategory3(), item.getCategory4())
                    .stream()
                    .filter(category -> category != null && !category.isEmpty())
                    .collect(Collectors.toList());
        }
    }
} 