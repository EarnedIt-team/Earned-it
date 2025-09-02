package _team.earnedit.controller;

import _team.earnedit.dto.PagedResponse;
import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.profile.PublicUserInfoResponse;
import _team.earnedit.dto.wish.*;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.WishService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wish")
@Tag(name = "Wish API", description = "위시 관련 기능 (조회, 수정, 삭제 등)")
@Slf4j
public class WishController {
    private final WishService wishService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "위시 추가", description = "위시를 추가합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<ApiResponse<WishAddResponse>> addWish(
            @Parameter(
                    name = "wish",
                    description = "위시 정보 JSON 문자열",
                    required = true,
                    schema = @Schema(type = "string", example = "{ \"name\": \"아이폰 15 pro max\", \"vendor\": \"애플\", \"price\": 1500000, \"url\": \"https://store.example.com/item123\", \"starred\": true }")
            )
            @RequestPart("wish") String wishJson,
            @RequestPart("image") MultipartFile itemImage,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) throws JsonProcessingException {
        log.info("[WishController] addWish 요청 - userId = {}", userInfo.getUserId());

        WishAddRequest wishAddRequest = objectMapper.readValue(wishJson, WishAddRequest.class);

        WishAddResponse response = wishService.addWish(wishAddRequest, userInfo.getUserId(), itemImage);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("위시가 추가되었습니다.", response));
    }

    @GetMapping
    @Operation(
            summary = "위시 목록 조회",
            description = "사용자의 전체 위시 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<PagedResponse<WishListResponse>>> getWishList(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,

            @Parameter(
                    name = "pageable",
                    description = "페이징 정보 (예: page=0, size=10, sort=createdAt,desc)"
            )
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable
            ) {

        PagedResponse<WishListResponse> wishList = wishService.getWishList(userInfo.getUserId(), pageable);

        return ResponseEntity.ok(ApiResponse.success("위시 목록을 조회하였습니다.", wishList));
    }

    @GetMapping("/{wishId}")
    @Operation(
            summary = "단일 위시 조회",
            description = "위시 ID를 이용해 해당 위시 상세 정보를 조회합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<WishDetailResponse>> getWish(
            @PathVariable Long wishId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {

        WishDetailResponse wish = wishService.getWish(wishId, userInfo.getUserId());

        return ResponseEntity.ok(ApiResponse.success("위시를 조회하였습니다.", wish));

    }

    @PatchMapping(value = "/{wishId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "위시 수정",
            description = "위시 ID와 수정할 정보를 입력받아 해당 위시를 수정합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<WishUpdateResponse>> updateWish(
            @Parameter(
                    name = "wish",
                    description = "위시 정보 JSON 문자열",
                    required = true,
                    schema = @Schema(type = "string", example = "{ \"name\": \"아이폰 15 pro max\", \"vendor\": \"애플\", \"price\": 1500000, \"url\": \"https://store.example.com/item123\", \"starred\": true }")
            )
            @RequestPart("wish") String wishJson,
            @RequestPart("image") MultipartFile itemImage,
            @PathVariable Long wishId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) throws JsonProcessingException {

        WishUpdateRequest updateRequest = objectMapper.readValue(wishJson, WishUpdateRequest.class);

        WishUpdateResponse response = wishService.updateWish(updateRequest, userInfo.getUserId(), wishId, itemImage);

        return ResponseEntity.ok(ApiResponse.success("위시가 수정되었습니다.", response));
    }

    @DeleteMapping("/{wishId}")
    @Operation(
            summary = "위시 삭제",
            description = "위시 ID를 이용해 해당 위시를 삭제합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<Long>> deleteWish(
            @PathVariable Long wishId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        wishService.deleteWish(wishId, userInfo.getUserId());

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("위시가 삭제되었습니다."));
    }

    @PatchMapping("/{wishId}/toggle-bought")
    @Operation(
            summary = "위시 구매상태 토글",
            description = "위시 ID를 이용해 해당 위시의 구매상태를 true/false로 전환합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<String>> toggleBought(
            @PathVariable Long wishId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        boolean isBought = wishService.toggleBoughtStatus(wishId, userInfo.getUserId());
        return ResponseEntity.ok(ApiResponse.success(String.format("구매상태가 변경되었습니다 %s", isBought)));
    }

    @GetMapping("/highlight")
    @Operation(
            summary = "위시 하이라이트 조회",
            description = "사용자의 하이라이트 위시(3개)를 조회합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<WishHighlightResponse>> highlightWish(
            @AuthenticationPrincipal JwtUserInfoDto userInfo
    ) {
        WishHighlightResponse response = wishService.highlightWish(userInfo.getUserId());

        return ResponseEntity.ok(ApiResponse.success("위시 하이라이트를 조회하였습니다.", response));

    }

    @GetMapping("/search")
    @Operation(
            summary = "위시 검색",
            description = "검색어를 입력해 위시를 탐색합니다. ",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<PagedResponse<WishListResponse>>> searchWish(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @ModelAttribute WishSearchCondition condition,
            @Parameter(
                    name = "pageable",
                    description = "페이징 정보 (예: page=0, size=10, sort=createdAt,desc)"
            )
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagedResponse<WishListResponse> result = wishService.searchWish(userInfo.getUserId(), condition, pageable);

        return ResponseEntity.ok(ApiResponse.success("검색 결과입니다.", result));
    }

    @GetMapping("/main")
    @Operation(
            summary = "위시 통합조회",
            description = "위시 페이지 렌더링 시, 유저 목록, star리스트, \n" +
                    "위시리스트(3개)\n" +
                    "내 정보를 모두 조회합니다",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<FetchWishPageResponse>> fetchWishPage(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestParam long userCount
    ) {
        FetchWishPageResponse response = wishService.fetchWishPage(userInfo.getUserId(), userCount);

        return ResponseEntity.ok(ApiResponse.success("위시 페이지 통합 조회 성공.", response));

    }








}
