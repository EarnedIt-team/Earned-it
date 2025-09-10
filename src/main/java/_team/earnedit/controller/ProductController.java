package _team.earnedit.controller;

import _team.earnedit.dto.item.NaverProductSearchRequest;
import _team.earnedit.dto.item.ProductSearchResponse;
import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Product", description = "상품 검색 API")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/search")
    @Operation(
            summary = "상품 검색",
            description = "네이버 쇼핑 API를 통해 상품을 검색합니다. 이미지 자동 저장 및 배경 제거 기능을 제공합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<ProductSearchResponse>> searchProducts(
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto,
            @Valid @ModelAttribute NaverProductSearchRequest request
    ) {        
        ProductSearchResponse result = productService.searchProducts(request);
        return ResponseEntity.ok(ApiResponse.success("상품 검색이 완료되었습니다.", result));
    }
} 