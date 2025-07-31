package _team.earnedit.dto.puzzle;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PieceResponse {
    private Long pieceId;
    private String rarity;
    private LocalDateTime collectedAt;
    private String image;
    private String vendor;
    private String name;
    private long price;
    private String description;
}
