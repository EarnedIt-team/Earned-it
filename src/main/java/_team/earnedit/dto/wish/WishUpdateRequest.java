package _team.earnedit.dto.wish;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "위시 수정 요청 DTO")
public class WishUpdateRequest {

    @NotBlank(message = "위시 이름은 공백값이 불가능합니다.")
    @Schema(description = "위시 이름", example = "무선 마우스")
    private String name;

    @Schema(description = "판매처", example = "네이버 스마트스토어")
    private String vendor;

    @NotNull(message = "가격은 필수입니다.")
    @Schema(description = "가격", example = "29900")
    private int price;

    @NotBlank(message = "상품 이미지는 반드시 있어야합니다.")
    @Schema(description = "상품 이미지 URL", example = "https://cdn.example.com/images/mouse.png")
    private String itemImage;

    @Schema(description = "상품 구매 URL", example = "https://smartstore.naver.com/item/123")
    private String url;
}