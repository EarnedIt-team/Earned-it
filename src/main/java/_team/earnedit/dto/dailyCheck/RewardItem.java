package _team.earnedit.dto.dailyCheck;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RewardItem {
    private Long itemId;
    private String name;
    private String image;
    private long price;
}