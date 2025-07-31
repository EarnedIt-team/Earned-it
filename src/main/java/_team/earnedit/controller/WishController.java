package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.wish.*;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.global.ErrorResponse;
import _team.earnedit.service.WishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wish")
@Tag(name = "Wish API", description = "위시 관련 기능 (조회, 수정, 삭제 등)")
public class WishController {
    private final WishService wishService;

    @PostMapping
    @Operation(summary = "위시 추가", description = "위시를 추가합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<ApiResponse<WishAddResponse>> addWish(
            @RequestBody @Valid WishAddRequest wishAddRequest,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {

        WishAddResponse response = wishService.addWish(wishAddRequest, userInfo.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("위시가 추가되었습니다.", response));
    }

    @GetMapping
    @Operation(
            summary = "위시 목록 조회",
            description = "사용자의 전체 위시 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<List<WishListResponse>>> getWishList(
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {

        List<WishListResponse> wishList = wishService.getWishList(userInfo.getUserId());

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


    @PatchMapping("/{wishId}")
    @Operation(
            summary = "위시 수정",
            description = "위시 ID와 수정할 정보를 입력받아 해당 위시를 수정합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<WishUpdateResponse>> updateWish(
            @RequestBody @Valid WishUpdateRequest wishUpdateRequest,
            @PathVariable Long wishId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        WishUpdateResponse response = wishService.updateWish(wishUpdateRequest, userInfo.getUserId(), wishId);

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

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success("위시가 삭제되었습니다."));
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

}
