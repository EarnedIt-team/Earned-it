package _team.earnedit.dto.wish;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class WishAddResponse {

    private long wishId;

    private LocalDateTime createdAt;
}
