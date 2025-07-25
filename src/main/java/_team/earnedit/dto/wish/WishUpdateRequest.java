package _team.earnedit.dto.wish;

import lombok.Getter;

@Getter
public class WishUpdateRequest {
    private String name;
    private String vendor;
    private String itemImage;
    private int price;
    private String url;
}
