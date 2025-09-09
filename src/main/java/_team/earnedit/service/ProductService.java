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
                .products(products)
                .totalCount(naverResponse.getTotal())
                .query(request.getQuery())
                .useCache(request.getUseCache())
                .removeBackground(request.getRemoveBackground())
                .build();
    }

    private ProductSearchResponse.ProductItem processItem(NaverProductResponse.NaverProductItem item, Boolean useCache) {
        try {
            String s3ImageUrl = fileUploadService.uploadImageFromUrl(item.getImage());
            ProductSearchResponse.ProductItem productItem = ProductSearchResponse.ProductItem.from(item, s3ImageUrl);
            
            if (useCache) {
                saveOrUpdateCache(productItem);
            }
            
            return productItem;
        } catch (Exception e) {
            log.error("상품 처리 실패 - productId: {}", item.getProductId(), e);
            return ProductSearchResponse.ProductItem.from(item, item.getImage());
        }
    }

    @Cacheable(value = "productSearch", key = "#query")
    private ProductSearchResponse getCachedResult(String query) {
        try {
            List<SearchItem> items = searchItemRepository.findByNameContainingIgnoreCase(query)
                    .stream().limit(20).collect(Collectors.toList());
            
            if (items.isEmpty()) return null;
            
            List<ProductSearchResponse.ProductItem> products = items.stream()
                    .map(this::toProductItem)
                    .collect(Collectors.toList());
            
            return ProductSearchResponse.builder()
                    .products(products)
                    .totalCount(products.size())
                    .query(query)
                    .useCache(true)
                    .removeBackground(false)
                    .build();
        } catch (Exception e) {
            log.error("캐시 조회 실패 - query: {}", query, e);
            return null;
        }
    }

    private void saveOrUpdateCache(ProductSearchResponse.ProductItem productItem) {
        try {
            SearchItem existing = searchItemRepository.findByProductId(productItem.getId());
            if (existing != null) {
                updateSearchItem(existing, productItem);
            } else {
                searchItemRepository.save(createSearchItem(productItem));
            }
        } catch (Exception e) {
            log.warn("캐시 저장 실패 - productId: {}", productItem.getId(), e);
        }
    }

    private SearchItem createSearchItem(ProductSearchResponse.ProductItem item) {
        return SearchItem.builder()
                .productId(item.getId())
                .name(item.getName())
                .vendor(item.getMallName())
                .price(item.getPrice().longValue())
                .imageUrl(item.getImageUrl())
                .productUrl(item.getUrl())
                .maker(item.getMaker())
                .productType(item.getProductType())
                .category1(getCategory(item, 0))
                .category2(getCategory(item, 1))
                .category3(getCategory(item, 2))
                .category4(getCategory(item, 3))
                .description(item.getName())
                .build();
    }

    private void updateSearchItem(SearchItem item, ProductSearchResponse.ProductItem productItem) {
        item.setName(productItem.getName());
        item.setVendor(productItem.getMallName());
        item.setPrice(productItem.getPrice().longValue());
        item.setImageUrl(productItem.getImageUrl());
        item.setProductUrl(productItem.getUrl());
        item.setMaker(productItem.getMaker());
        item.setProductType(productItem.getProductType());
        item.setCategory1(getCategory(productItem, 0));
        item.setCategory2(getCategory(productItem, 1));
        item.setCategory3(getCategory(productItem, 2));
        item.setCategory4(getCategory(productItem, 3));
        searchItemRepository.save(item);
    }

    private ProductSearchResponse.ProductItem toProductItem(SearchItem item) {
        List<String> categories = List.of(item.getCategory1(), item.getCategory2(), 
                item.getCategory3(), item.getCategory4())
                .stream().filter(cat -> cat != null && !cat.isEmpty())
                .collect(Collectors.toList());
        
        return ProductSearchResponse.ProductItem.builder()
                .id(item.getProductId())
                .name(item.getName())
                .price(Double.valueOf(item.getPrice()))
                .imageUrl(item.getImageUrl())
                .url(item.getProductUrl())
                .mallName(item.getVendor())
                .productType(item.getProductType())
                .maker(item.getMaker())
                .categories(categories)
                .build();
    }

    private ProductSearchResponse createEmptyResponse(NaverProductSearchRequest request) {
        return ProductSearchResponse.builder()
                .products(List.of())
                .totalCount(0)
                .query(request.getQuery())
                .useCache(request.getUseCache())
                .removeBackground(request.getRemoveBackground())
                .build();
    }

    private String getCategory(ProductSearchResponse.ProductItem item, int index) {
        return item.getCategories().size() > index ? item.getCategories().get(index) : "";
    }
} 