package _team.earnedit.dto.wish;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class WishUpdateResponse {

    private long wishId;
    private String name;
    private String vendor;
    private int price;
    private String ItemImage;
    private String url;
    private LocalDateTime updatedAt;


}
