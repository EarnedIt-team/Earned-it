package _team.earnedit.dto.profile;

import _team.earnedit.dto.star.StarSummaryResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Builder
@Getter
public class ProfileWithStarResponse {

    ProfileInfoResponseDto userInfo;
    List<StarSummaryResponse> starList;
}
