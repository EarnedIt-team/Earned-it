package _team.earnedit.mapper;

import _team.earnedit.dto.star.StarListResponse;
import _team.earnedit.entity.Star;
import _team.earnedit.entity.Wish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StarMapper {
    @Mapping(target = "starId", source = "star.id")
    @Mapping(target = "wishId", source = "wish.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "name", source = "wish.name")
    @Mapping(target = "rank", source = "star.rank")
    @Mapping(target = "itemImage", source = "wish.itemImage")
    @Mapping(target = "vendor", source = "wish.vendor")
    @Mapping(target = "price", source = "wish.price")
    @Mapping(target = "isBought", source = "wish.bought")
    @Mapping(target = "starred", source = "wish.starred")
    @Mapping(target = "url", source = "wish.url")
    StarListResponse toStarListResponse(Star star, Wish wish);
}
