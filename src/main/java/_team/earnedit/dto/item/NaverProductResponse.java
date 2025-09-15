package _team.earnedit.dto.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverProductResponse {
    
    @JsonProperty("lastBuildDate")
    private String lastBuildDate;
    
    @JsonProperty("total")
    private Integer total;
    
    @JsonProperty("start")
    private Integer start;
    
    @JsonProperty("display")
    private Integer display;
    
    @JsonProperty("items")
    private List<NaverProductItem> items;
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NaverProductItem {
        
        @JsonProperty("title")
        private String title;
        
        @JsonProperty("link")
        private String link;
        
        @JsonProperty("image")
        private String image;
        
        @JsonProperty("lprice")
        private String lprice;
        
        @JsonProperty("hprice")
        private String hprice;
        
        @JsonProperty("mallName")
        private String mallName;
        
        @JsonProperty("productId")
        private String productId;
        
        @JsonProperty("productType")
        private String productType;
        
        @JsonProperty("brand")
        private String brand;
        
        @JsonProperty("maker")
        private String maker;
        
        @JsonProperty("category1")
        private String category1;
        
        @JsonProperty("category2")
        private String category2;
        
        @JsonProperty("category3")
        private String category3;
        
        @JsonProperty("category4")
        private String category4;
    }
} 