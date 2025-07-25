package _team.earnedit.dto.wish;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WishAddRequest {

    private String name;

    private String vendor;

    private int price;

    private String itemImage;

    private String url;
}
