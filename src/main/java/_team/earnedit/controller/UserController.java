package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> softDeleteUser(
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
    ) {
        userService.softDeleteUser(userInfoDto.getUserId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("계정이 삭제되었습니다."));
    }
}
