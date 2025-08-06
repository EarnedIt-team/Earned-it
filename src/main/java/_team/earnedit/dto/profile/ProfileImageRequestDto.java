package _team.earnedit.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Schema(description = "프로필 이미지 업로드 DTO")
public class ProfileImageRequestDto {

    @Schema(description = "업로드할 프로필 이미지 파일", type = "string", format = "binary")
    private MultipartFile profileImage;

    public ProfileImageRequestDto(MultipartFile profileImage) {
        this.profileImage = profileImage;
    }
}
