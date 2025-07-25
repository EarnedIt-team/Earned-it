package _team.earnedit.dto.wish;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class WishUpdateRequest {

    @NotBlank(message = "위시 이름은 공백값이 불가능합니다.")
    private String name;

    private String vendor;

    @NotNull(message = "가격은 필수입니다.")
    private int price;

    @NotBlank(message = "상품 이미지는 반드시 있어야합니다." )
    private String itemImage;
    private String url;
}