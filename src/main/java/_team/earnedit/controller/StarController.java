package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.StarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/star")
@RequiredArgsConstructor
public class StarController {

    private final StarService starService;

    @PatchMapping("/{wishId}")
    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<String>> updateStar(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @PathVariable long wishId) {
        starService.updateStar(userInfo.getUserId(), wishId);

        return ResponseEntity.ok(ApiResponse.success("Star에 해당 위시를 추가하였습니다."));
    }

}
