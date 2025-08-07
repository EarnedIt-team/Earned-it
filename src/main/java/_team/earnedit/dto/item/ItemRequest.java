package _team.earnedit.dto.item;

import _team.earnedit.entity.Item;
import _team.earnedit.entity.Rarity;
import _team.earnedit.entity.Theme;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "벤더는 필수입니다.")
    private String vendor;

    @Min(value = 1, message = "가격은 1원 이상이어야 합니다.")
    private long price;

    @NotBlank(message = "이미지 URL은 필수입니다.")
    private String image;

    @NotBlank(message = "설명은 필수입니다.")
    private String description;

    @NotNull(message = "희귀도를 선택해야 합니다.")
    private Rarity rarity;

    @NotNull(message = "카테고리를 작성해야합니다.")
    private String category;

    public Item toEntity(ItemRequest dto) {
        return Item.builder()
                .name(dto.getName())
                .vendor(dto.getVendor())
                .price(dto.getPrice())
                .image(dto.getImage())
                .description(dto.getDescription())
                .rarity(dto.getRarity())
                .category(dto.getCategory())
                .build();
    }

    public static ItemRequest from(Item item) {
        ItemRequest request = new ItemRequest();
        request.setName(item.getName());
        request.setVendor(item.getVendor());
        request.setPrice(item.getPrice());
        request.setImage(item.getImage());
        request.setDescription(item.getDescription());
        request.setRarity(item.getRarity());
        request.setCategory(item.getCategory());
        return request;
    }
}