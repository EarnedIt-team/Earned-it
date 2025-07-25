package _team.earnedit.dto.wish;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class WishAddResponse {

    private long wishId;

    private LocalDateTime createdAt;
}
