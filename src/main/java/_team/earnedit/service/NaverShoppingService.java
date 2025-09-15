package _team.earnedit.service;

import _team.earnedit.dto.item.NaverProductResponse;
import _team.earnedit.dto.item.NaverProductSearchRequest;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@Service
@Slf4j
public class NaverShoppingService {

    private final RestTemplate restTemplate;

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    private static final String NAVER_SHOPPING_API_URL = "https://openapi.naver.com/v1/search/shop.json";

    public NaverShoppingService() {
        // RestTemplate을 직접 생성하여 타임아웃 설정
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10초
        factory.setReadTimeout(30000);    // 30초
        this.restTemplate = new RestTemplate(factory);
    }

    public NaverProductResponse searchProducts(NaverProductSearchRequest request) {
        log.info("네이버 쇼핑 API 호출 - query: {}", request.getQuery());

        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(NAVER_SHOPPING_API_URL)
                    .queryParam("query", request.getQuery())
                    .queryParam("display", request.getDisplay())
                    .queryParam("start", request.getStart())
                    .queryParam("sort", request.getSort())
                    .build()
                    .encode()
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            ResponseEntity<NaverProductResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    NaverProductResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                log.error("네이버 API 응답 오류 - status: {}", response.getStatusCode());
                throw new CustomException(ErrorCode.NAVER_API_ERROR);
            }

        } catch (Exception e) {
            log.error("네이버 API 호출 실패 - query: {}", request.getQuery(), e);
            throw new CustomException(ErrorCode.NAVER_API_ERROR);
        }
    }
} 