package _team.earnedit.dto.wish;

import _team.earnedit.dto.profile.ProfileInfoResponseDto;
import _team.earnedit.dto.profile.PublicUserInfoResponse;
import _team.earnedit.dto.star.StarSummaryResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class FetchWishPageResponse {

    ProfileInfoResponseDto userInfo;
    List<PublicUserInfoResponse> userList;
    List<StarSummaryResponse> starList;
    List<WishDetailResponse> wishList;
}
