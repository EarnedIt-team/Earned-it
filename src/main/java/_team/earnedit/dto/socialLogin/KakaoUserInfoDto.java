package _team.earnedit.dto.socialLogin;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "카카오 사용자 정보 DTO")
public class KakaoUserInfoDto {

    @Schema(description = "카카오 고유 ID", example = "1234567890")
    private String id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {

        private Profile profile;

        @Schema(description = "사용자 이메일", example = "user@kakao.com")
        private String email;

        @Schema(description = "성별", example = "female")
        private String gender;
    }

    @Getter
    @NoArgsConstructor
    public static class Profile {
//        private String nickname;
        @JsonProperty("profile_image_url")
        @Schema(description = "프로필 이미지 URL", example = "https://k.kakaocdn.net/...jpg")
        private String profileImageUrl;
    }

    public String getEmail() {
        return kakaoAccount != null ? kakaoAccount.getEmail() : null;
    }

    /*
    public String getNickname() {
        return kakaoAccount != null && kakaoAccount.getProfile() != null
                ? kakaoAccount.getProfile().getNickname() : null;
    }
     */

    public String getProfileImage() {
        return kakaoAccount != null && kakaoAccount.getProfile() != null
                ? kakaoAccount.getProfile().getProfileImageUrl() : null;
    }
}
