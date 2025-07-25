package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.wish.WishAddRequest;
import _team.earnedit.dto.wish.WishAddResponse;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wish")
public class WishController {
    private final WishService wishService;

    @PostMapping
    public ResponseEntity<ApiResponse<WishAddResponse>> addWish(
            @RequestBody WishAddRequest wishAddRequest,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {

        WishAddResponse response = wishService.addWish(wishAddRequest, userInfo.getUserId());

        return ResponseEntity.ok(ApiResponse.success("위시가 추가되었습니다.", response));
    }
}
