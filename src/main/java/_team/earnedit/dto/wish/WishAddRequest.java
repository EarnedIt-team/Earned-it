package _team.earnedit.dto.wish;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "위시 추가 요청 객체")
public class WishAddRequest {

    @NotBlank(message = "위시 이름은 공백값이 불가능합니다.")
    @Schema(description = "위시 이름", example = "아이폰 16 pro max")
    private String name;

    @Schema(description = "브랜드", example = "애플")
    private String vendor;

    @NotNull(message = "가격은 필수입니다.")
    @Positive(message = "가격은 0보다 커야 합니다.")
    @Schema(description = "위시 가격", example = "1,500,000")
    private int price;

    @NotBlank(message = "상품 이미지는 반드시 있어야합니다.")
    @Schema(description = "위시 이미지", example = "https://cdn.example.com/images/item123.png")
    private String itemImage;

    @Schema(description = "상품 구매 URL", example = "https://store.example.com/products/item123")
    private String url;
}
