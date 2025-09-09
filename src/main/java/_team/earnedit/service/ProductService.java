package _team.earnedit.service;

import _team.earnedit.dto.item.NaverProductSearchRequest;
import _team.earnedit.dto.item.NaverProductResponse;
import _team.earnedit.dto.item.ProductSearchResponse;
import _team.earnedit.entity.Item;
import _team.earnedit.entity.Rarity;
import _team.earnedit.repository.ItemRepository;
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
    private final ItemRepository itemRepository;

    @Transactional
    public ProductSearchResponse searchProducts(NaverProductSearchRequest request) {
        log.info("상품 검색 - query: {}, useCache: {}", request.getQuery(), request.getUseCache());

        // 캐시 사용 시 캐시된 결과 확인
        if (request.getUseCache()) {
            ProductSearchResponse cachedResult = getCachedSearchResult(request.getQuery());
            if (cachedResult != null) {
                return cachedResult;
            }
        }

        // 네이버 API 호출
        NaverProductResponse naverResponse = naverShoppingService.searchProducts(request);
        
        if (naverResponse.getItems() == null || naverResponse.getItems().isEmpty()) {
            return ProductSearchResponse.builder()
                    .products(List.of())
                    .totalCount(0)
                    .query(request.getQuery())
                    .useCache(request.getUseCache())
                    .removeBackground(request.getRemoveBackground())
                    .build();
        }

        // 비동기로 이미지 처리 및 상품 정보 변환
        List<CompletableFuture<ProductSearchResponse.ProductItem>> futures = naverResponse.getItems().stream()
                .map(item -> CompletableFuture.supplyAsync(() -> {
                    try {
                        // 네이버 이미지를 S3에 업로드
                        String processedImageUrl = fileUploadService.uploadImageFromUrl(item.getImage());
                        
                        // TODO: 배경 제거 기능 구현 필요 (Remove.bg API 또는 다른 솔루션 사용)
                        if (request.getRemoveBackground()) {
                            // processedImageUrl = backgroundRemovalService.removeBackground(processedImageUrl);
                        }
                        
                        // 상품 정보 변환
                        ProductSearchResponse.ProductItem productItem = 
                                ProductSearchResponse.ProductItem.from(item, processedImageUrl);
                        
                        // 캐싱 또는 업데이트
                        if (request.getUseCache()) {
                            saveProductToCache(productItem);
                        } else {
                            updateProductCache(productItem);
                        }
                        
                        return productItem;
                    } catch (Exception e) {
                        log.error("상품 처리 실패 - productId: {}", item.getProductId(), e);
                        return ProductSearchResponse.ProductItem.from(item, item.getImage());
                    }
                }))
                .collect(Collectors.toList());

        // 모든 비동기 작업 완료 대기
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

    @Cacheable(value = "productSearch", key = "#query")
    private ProductSearchResponse getCachedSearchResult(String query) {
        try {
            List<Item> cachedItems = itemRepository.findAll().stream()
                    .filter(item -> item.getName().toLowerCase().contains(query.toLowerCase()))
                    .limit(20) // 캐시 결과는 20개로 제한
                    .collect(Collectors.toList());
            
            if (cachedItems.isEmpty()) {
                return null;
            }
            
            List<ProductSearchResponse.ProductItem> products = cachedItems.stream()
                    .map(this::convertItemToProductItem)
                    .collect(Collectors.toList());
            
            return ProductSearchResponse.builder()
                    .products(products)
                    .totalCount(products.size())
                    .query(query)
                    .useCache(true)
                    .removeBackground(false) // 캐시된 결과는 배경 제거 안함
                    .build();
                    
        } catch (Exception e) {
            log.error("캐시 조회 실패 - query: {}", query, e);
            return null;
        }
    }

    private ProductSearchResponse.ProductItem convertItemToProductItem(Item item) {
        List<String> categories = List.of(
                item.getCategory(),
                item.getCategory2(),
                item.getCategory3(),
                item.getCategory4()
        ).stream()
        .filter(cat -> cat != null && !cat.isEmpty())
        .collect(Collectors.toList());
        
        return ProductSearchResponse.ProductItem.builder()
                .id(item.getProductId() != null ? item.getProductId() : item.getId().toString())
                .name(item.getName())
                .price(Double.valueOf(item.getPrice()))
                .imageUrl(item.getImage())
                .url(item.getUrl() != null ? item.getUrl() : "")
                .mallName(item.getVendor())
                .productType(item.getProductType() != null ? item.getProductType() : "1")
                .maker(item.getMaker() != null ? item.getMaker() : "")
                .categories(categories)
                .build();
    }

    private void saveProductToCache(ProductSearchResponse.ProductItem productItem) {
        try {
            if (itemRepository.existsByProductId(productItem.getId())) {
                return; // 이미 존재하면 저장하지 않음
            }

            Item item = createItemFromProductItem(productItem);
            itemRepository.save(item);
            
        } catch (Exception e) {
            log.warn("캐시 저장 실패 - productId: {}", productItem.getId(), e);
        }
    }
    
    private void updateProductCache(ProductSearchResponse.ProductItem productItem) {
        try {
            Item item = itemRepository.findByProductId(productItem.getId());
            if (item != null) {
                updateItemFromProductItem(item, productItem);
                itemRepository.save(item);
            }
        } catch (Exception e) {
            log.warn("캐시 업데이트 실패 - productId: {}", productItem.getId(), e);
        }
    }

    private Item createItemFromProductItem(ProductSearchResponse.ProductItem productItem) {
        return Item.builder()
                .name(productItem.getName())
                .vendor(productItem.getMallName() != null ? productItem.getMallName() : "네이버")
                .price(productItem.getPrice().longValue())
                .image(productItem.getImageUrl())
                .description(productItem.getName())
                .category(productItem.getCategories().isEmpty() ? "기타" : productItem.getCategories().get(0))
                .rarity(Rarity.B)
                .productId(productItem.getId())
                .url(productItem.getUrl())
                .maker(productItem.getMaker() != null ? productItem.getMaker() : "")
                .productType(productItem.getProductType() != null ? productItem.getProductType() : "1")
                .category2(productItem.getCategories().size() > 1 ? productItem.getCategories().get(1) : "")
                .category3(productItem.getCategories().size() > 2 ? productItem.getCategories().get(2) : "")
                .category4(productItem.getCategories().size() > 3 ? productItem.getCategories().get(3) : "")
                .build();
    }

    private void updateItemFromProductItem(Item item, ProductSearchResponse.ProductItem productItem) {
        item.setName(productItem.getName());
        item.setVendor(productItem.getMallName() != null ? productItem.getMallName() : "네이버");
        item.setPrice(productItem.getPrice().longValue());
        item.setImage(productItem.getImageUrl());
        item.setDescription(productItem.getName());
        item.setCategory(productItem.getCategories().isEmpty() ? "기타" : productItem.getCategories().get(0));
        item.setUrl(productItem.getUrl());
        item.setMaker(productItem.getMaker() != null ? productItem.getMaker() : "");
        item.setProductType(productItem.getProductType() != null ? productItem.getProductType() : "1");
        item.setCategory2(productItem.getCategories().size() > 1 ? productItem.getCategories().get(1) : "");
        item.setCategory3(productItem.getCategories().size() > 2 ? productItem.getCategories().get(2) : "");
        item.setCategory4(productItem.getCategories().size() > 3 ? productItem.getCategories().get(3) : "");
    }
} 