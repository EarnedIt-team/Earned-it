package _team.earnedit.dto.main;

import _team.earnedit.dto.wish.WishListResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MainPageResponse {

    private UserInfo userInfo;
    private List<WishListResponse> starWishes;

    @Getter
    @Builder
    public static class UserInfo {
        private double amount;
        private double amountPerSec;
        private boolean hasSalary;
        private int payday;
    }
}
