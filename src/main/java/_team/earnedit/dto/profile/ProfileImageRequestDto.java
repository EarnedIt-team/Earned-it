package _team.earnedit.dto.profile;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ProfileImageRequestDto {

    private MultipartFile profileImage;

    public ProfileImageRequestDto(MultipartFile profileImage) {
        this.profileImage = profileImage;
    }
}
