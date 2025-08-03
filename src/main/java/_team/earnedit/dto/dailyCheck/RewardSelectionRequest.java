package _team.earnedit.dto.dailyCheck;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RewardSelectionRequest {
    private UUID rewardToken;
    private Long selectedItemId;
}