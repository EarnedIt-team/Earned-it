package _team.earnedit.controller;

import _team.earnedit.dto.item.NaverProductSearchRequest;
import _team.earnedit.dto.item.ProductSearchResponse;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Product", description = "상품 API")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/search")
    @Operation(
            summary = "상품 검색",
            description = "네이버 쇼핑 API를 통해 상품을 검색합니다. 이미지 자동 저장 및 배경 제거 기능을 제공합니다."
    )
    public ResponseEntity<ApiResponse<ProductSearchResponse>> searchProducts(
            @Parameter(description = "검색어", required = true, example = "아이폰 14")
            @RequestParam String query,
            
            @Parameter(description = "캐시 사용 여부", example = "true")
            @RequestParam(defaultValue = "true") Boolean useCache,
            
            @Parameter(description = "이미지 배경 제거 여부 (향후 구현 예정)", example = "false")
            @RequestParam(defaultValue = "false") Boolean removeBackground,
            
            @Parameter(description = "검색 결과 개수 (최대 100)", example = "20")
            @RequestParam(defaultValue = "20") Integer display,
            
            @Parameter(description = "검색 시작 위치", example = "1")
            @RequestParam(defaultValue = "1") Integer start,
            
            @Parameter(description = "정렬 방식 (sim: 정확도순, date: 날짜순, asc: 가격오름차순, dsc: 가격내림차순)", example = "sim")
            @RequestParam(defaultValue = "sim") String sort
    ) {
        NaverProductSearchRequest request = NaverProductSearchRequest.builder()
                .query(query.trim())
                .useCache(useCache)
                .removeBackground(removeBackground)
                .display(display)
                .start(start)
                .sort(sort)
                .build();
        
        ProductSearchResponse result = productService.searchProducts(request);
        
        return ResponseEntity.ok(ApiResponse.success("상품 검색이 완료되었습니다.", result));
    }
} 