package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.profile.*;
import _team.earnedit.dto.term.TermRequestDto;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.ProfileService;
import _team.earnedit.service.TermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Profile API", description = "프로필 조회, 닉네임/이미지 변경, 수익 설정 등 사용자 정보 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final TermService termService;

    @Operation(
            summary = "월급 정보 저장/갱신",
            description = "로그인한 사용자의 수익 정보를 저장하거나 갱신합니다.",
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
            summary = "월급 정보 조회",
            description = "로그인한 사용자의 수익 정보를 조회합니다.",
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
            summary = "약관 동의 처리",
            description = "회원가입 이후 약관 동의 여부를 저장합니다.",
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
            summary = "프로필 정보 조회",
            description = "닉네임, 프로필 사진, 수익 정보 등 기본 프로필 정보를 조회합니다.",
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
            summary = "닉네임 변경",
            description = "사용자의 닉네임을 변경합니다.",
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
            summary = "프로필 이미지 변경",
            description = "사용자의 프로필 이미지를 변경합니다.",
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
            summary = "프로필 이미지 삭제",
            description = "사용자의 프로필 이미지를 삭제합니다.",
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

    @GetMapping("/random-users")
    @Operation(
            summary = "공개 유저 조회",
            description = "is_public이 공개인 유저를 랜덤 조회한다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    public ResponseEntity<ApiResponse<List<PublicUserInfoResponse>>> randomUsers(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestParam(defaultValue = "5") long count
    ) {
        List<PublicUserInfoResponse> userInfos = profileService.randomUsers(userInfo.getUserId(), count);

        return ResponseEntity.ok(ApiResponse.success("공개 프로필 유저를 조회했습니다.", userInfos));

    }

    @Operation(
            summary = "타 유저 프로필 조회",
            description = "해당 유저의 프로필을 조회합니다. \n" +
                    "StarList, UserInfo 정보만 제공합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileWithStarResponse>> getProfileWithStarList(
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto,
            @PathVariable long userId)
    {
        ProfileWithStarResponse response = profileService.getProfileWithStarList(userInfoDto.getUserId(), userId);
        return ResponseEntity.ok(ApiResponse.success(String.format("유저의 프로필 조회 성공, userId = %s", userId), response));
    }

}
