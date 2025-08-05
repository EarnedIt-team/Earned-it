package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.profile.*;
import _team.earnedit.dto.term.TermRequestDto;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.ProfileService;
import _team.earnedit.service.TermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final TermService termService;

    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @PostMapping("/salary")
    public ResponseEntity<ApiResponse<SalaryResponseDto>> saveSalary(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestBody SalaryRequestDto requestDto)
    {
        SalaryResponseDto responseDto = profileService.updateSalary(userInfo.getUserId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("수익 정보를 업데이트했습니다",responseDto));
    }

    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @GetMapping("/salary")
    public ResponseEntity<ApiResponse<SalaryResponseDto>> getSalary(
            @AuthenticationPrincipal JwtUserInfoDto userInfo)
    {
        SalaryResponseDto responseDto = profileService.getSalary(userInfo.getUserId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("수익 정보를 조회했습니다", responseDto));
    }


    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @PostMapping("/terms")
    public ResponseEntity<ApiResponse<Void>> agreeToTerms(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestBody List<TermRequestDto> requestDtos)
    {
        termService.agreeToTerms(userInfo.getUserId(), requestDtos);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("약관 동의 여부를 업데이트했습니다"));
    }

    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @GetMapping
    public ResponseEntity<ApiResponse<ProfileInfoResponseDto>> getProfile(
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto)
    {
        ProfileInfoResponseDto response = profileService.getProfile(userInfoDto.getUserId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("기본 프로필 정보를 조회했습니다", response));
    }

    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @PatchMapping("/nickname")
    public ResponseEntity<ApiResponse<Void>> updateNickname(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestBody NicknameRequestDto requestDto)
    {
        profileService.updateNickname(userInfo.getUserId(), requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("닉네임 변경이 완료되었습니다"));
    }

    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @PatchMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateProfileImage(
            @ModelAttribute ProfileImageRequestDto requestDto,
            @AuthenticationPrincipal JwtUserInfoDto userInfo)
    {
        profileService.updateProfileImage(userInfo.getUserId(), requestDto.getProfileImage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("프로필 사진 변경이 완료되었습니다"));
    }

    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @PatchMapping("/image/delete")
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage(
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto)
    {

        profileService.deleteProfileImage(userInfoDto.getUserId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("프로필 이미지가 삭제되었습니다."));
    }

}
