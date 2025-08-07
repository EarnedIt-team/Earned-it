package _team.earnedit.mapper;

import _team.earnedit.dto.wish.WishAddRequest;
import _team.earnedit.dto.wish.WishAddResponse;
import _team.earnedit.dto.wish.WishListResponse;
import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

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


    @Mapping(target = "wishId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "itemImage", source = "itemImage")
    @Mapping(target = "isBought", source = "bought")
    @Mapping(target = "vendor", source = "vendor")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "isStarred", source = "starred")
    @Mapping(target = "url", source = "url")
    WishListResponse toWishListResponse(Wish wish);

    @Mapping(target = "" )
    List<WishListResponse> toWishListResponseList(List<Wish> wishes);

}