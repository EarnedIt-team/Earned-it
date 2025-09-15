package _team.earnedit.service;

import _team.earnedit.dto.item.NaverProductSearchRequest;
import _team.earnedit.dto.item.NaverProductResponse;
import _team.earnedit.dto.item.ProductSearchResponse;
import _team.earnedit.entity.SearchItem;
import _team.earnedit.repository.SearchItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final NaverShoppingService naverShoppingService;
    private final FileUploadService fileUploadService;
    private final SearchItemRepository searchItemRepository;

    @Transactional
    public ProductSearchResponse searchProducts(NaverProductSearchRequest request) {
        log.info("상품 검색 - query: {}, useCache: {}", request.getQuery(), request.getUseCache());

        // 캐시 확인
        if (request.getUseCache()) {
            ProductSearchResponse cached = getCachedResult(request.getQuery());
            if (cached != null) return cached;
        }

        // 네이버 API 호출
        NaverProductResponse naverResponse = naverShoppingService.searchProducts(request);
        if (naverResponse.getItems() == null || naverResponse.getItems().isEmpty()) {
            return createEmptyResponse(request);
        }

        // 비동기 처리
        List<CompletableFuture<ProductSearchResponse.ProductItem>> futures = naverResponse.getItems().stream()
                .map(item -> CompletableFuture.supplyAsync(() -> processItem(item, request.getUseCache())))
                .collect(Collectors.toList());

        List<ProductSearchResponse.ProductItem> products = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return ProductSearchResponse.builder()
                .searchInfo(ProductSearchResponse.SearchInfo.builder()
                        .totalCount(naverResponse.getTotal())
                        .query(request.getQuery())
                        .useCache(request.getUseCache())
                        .removeBackground(request.getRemoveBackground())
                        .display(request.getDisplay())
                        .build())
                .products(products)
                .build();
    }

    private ProductSearchResponse.ProductItem processItem(NaverProductResponse.NaverProductItem item, Boolean useCache) {
        try {
            String s3ImageUrl = fileUploadService.uploadImageFromUrl(item.getImage());
            ProductSearchResponse.ProductItem productItem = ProductSearchResponse.ProductItem.from(item, s3ImageUrl);

            // useCache 여부와 관계없이 항상 캐시 업데이트 (최신 데이터 유지)
            saveOrUpdateCache(item, s3ImageUrl);

            return productItem;
        } catch (Exception e) {
            log.error("상품 처리 실패 - productId: {}", item.getProductId(), e);
            return ProductSearchResponse.ProductItem.from(item, item.getImage());
        }
    }

    @Cacheable(value = "productSearch", key = "#query")
    public ProductSearchResponse getCachedResult(String query) {
        try {
            List<SearchItem> items = searchItemRepository.findByNameContainingIgnoreCase(query)
                    .stream().limit(20).collect(Collectors.toList());

            if (items.isEmpty()) return null;

            List<ProductSearchResponse.ProductItem> products = items.stream()
                    .map(this::toProductItem)
                    .collect(Collectors.toList());

            return ProductSearchResponse.builder()
                    .searchInfo(ProductSearchResponse.SearchInfo.builder()
                            .totalCount(products.size())
                            .query(query)
                            .useCache(true)
                            .removeBackground(false)
                            .display(20) // 캐시 조회시 기본값
                            .build())
                    .products(products)
                    .build();
        } catch (Exception e) {
            log.error("캐시 조회 실패 - query: {}", query, e);
            return null;
        }
    }

    private void saveOrUpdateCache(NaverProductResponse.NaverProductItem naverItem, String s3ImageUrl) {
        try {
            SearchItem existing = searchItemRepository.findByProductId(naverItem.getProductId());
            if (existing != null) {
                updateSearchItem(existing, naverItem, s3ImageUrl);
            } else {
                searchItemRepository.save(createSearchItem(naverItem, s3ImageUrl));
            }
        } catch (Exception e) {
            log.warn("캐시 저장 실패 - productId: {}", naverItem.getProductId(), e);
        }
    }

    private SearchItem createSearchItem(NaverProductResponse.NaverProductItem item, String s3ImageUrl) {
        return SearchItem.builder()
                .productId(item.getProductId())
                .name(removeHtmlTags(item.getTitle()))
                .maker(item.getMaker() != null ? item.getMaker() : "")
                .price(parsePrice(item.getLprice()))
                .imageUrl(s3ImageUrl)
                .productUrl(item.getLink())
                .build();
    }

    private void updateSearchItem(SearchItem searchItem, NaverProductResponse.NaverProductItem naverItem, String s3ImageUrl) {
        searchItem.setName(removeHtmlTags(naverItem.getTitle()));
        searchItem.setMaker(naverItem.getMaker() != null ? naverItem.getMaker() : "");
        searchItem.setPrice(parsePrice(naverItem.getLprice()));
        searchItem.setImageUrl(s3ImageUrl);
        searchItem.setProductUrl(naverItem.getLink());
        searchItemRepository.save(searchItem);
    }

    private ProductSearchResponse.ProductItem toProductItem(SearchItem item) {
        return ProductSearchResponse.ProductItem.builder()
                .id(item.getProductId())
                .name(item.getName())
                .price(Double.valueOf(item.getPrice()))
                .imageUrl(item.getImageUrl())
                .url(item.getProductUrl())
                .maker(item.getMaker()) // SearchItem의 maker 필드 사용
                .build();
    }

    private String removeHtmlTags(String text) {
        if (text == null) return null;
        return text.replaceAll("<[^>]*>", "");
    }

    private Long parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) return 0L;
        try {
            return Long.parseLong(priceStr);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private ProductSearchResponse createEmptyResponse(NaverProductSearchRequest request) {
        return ProductSearchResponse.builder()
                .searchInfo(ProductSearchResponse.SearchInfo.builder()
                        .totalCount(0)
                        .query(request.getQuery())
                        .useCache(request.getUseCache())
                        .removeBackground(request.getRemoveBackground())
                        .display(request.getDisplay())
                        .build())
                .products(List.of())
                .build();
    }
}