package _team.earnedit.dto.wish;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@Schema(description = "위시 추가 응답 객체")
public class WishAddResponse {

    @Schema(description = "위시 Id", example = "1")
    private long wishId;

    @Schema(description = "생성 시각", example = "2025-07-28T14:30:00")
    private LocalDateTime createdAt;
}
