package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.main.MainPageResponse;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.MainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mainpage")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @GetMapping
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<ApiResponse<MainPageResponse>> getInfo(
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        MainPageResponse response = mainService.getInfo(userInfo.getUserId());

        return ResponseEntity
                .ok(ApiResponse.success("메인 페이지의 정보를 조회했습니다.", response));
    }
}
