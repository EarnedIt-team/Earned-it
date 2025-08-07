package _team.earnedit.mapper;

import _team.earnedit.dto.wish.WishAddRequest;
import _team.earnedit.dto.wish.WishAddResponse;
import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WishMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", expression = "java(user)")
    @Mapping(target = "itemImage", expression = "java(imageUrl)")
    @Mapping(target = "isStarred", expression = "java(isStarred)")
    Wish toEntity(WishAddRequest request, @Context User user, @Context String imageUrl, @Context boolean isStarred);

    @Mapping(target = "wishId", source = "id")
    WishAddResponse toResponse(Wish wish);

}