package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.wish.WishAddRequest;
import _team.earnedit.dto.wish.WishAddResponse;
import _team.earnedit.dto.wish.WishResponse;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.WishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

        return ResponseEntity.ok(ApiResponse.success("위시가 추가되었습니다.", response));
    }

    @GetMapping
    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<List<WishResponse>>> getWishList(
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {

        List<WishResponse> wishList = wishService.getWishList(userInfo.getUserId());

        return  ResponseEntity.ok(ApiResponse.success("위시 목록을 조회하였습니다.", wishList));

    }
}
