package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.wish.*;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.WishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
public class WishController {
    private final WishService wishService;

    @PostMapping
    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<WishAddResponse>> addWish(
            @RequestBody @Valid WishAddRequest wishAddRequest,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {

        WishAddResponse response = wishService.addWish(wishAddRequest, userInfo.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("위시가 추가되었습니다.", response));
    }

    @GetMapping
    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<List<WishResponse>>> getWishList(
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {

        List<WishResponse> wishList = wishService.getWishList(userInfo.getUserId());

        return ResponseEntity.ok(ApiResponse.success("위시 목록을 조회하였습니다.", wishList));

    }

    @GetMapping("/{wishId}")
    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<WishResponse>> getWish(
            @PathVariable Long wishId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {

        WishResponse wish = wishService.getWish(wishId, userInfo.getUserId());

        return ResponseEntity.ok(ApiResponse.success("위시를 조회하였습니다.", wish));

    }


    @PatchMapping("/{wishId}")
    @Operation(
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
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<Long>> deleteWish(
            @PathVariable Long wishId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo
    ) {
        wishService.deleteWish(wishId, userInfo.getUserId());

        return ResponseEntity.ok(ApiResponse.success("위시가 삭제되었습니다."));
    }

}
