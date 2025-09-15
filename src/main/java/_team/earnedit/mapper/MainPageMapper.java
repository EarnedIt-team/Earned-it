package _team.earnedit.mapper;

import _team.earnedit.dto.main.MainPageResponse;
import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.dto.wish.WishListResponse;
import _team.earnedit.entity.Piece;
import _team.earnedit.entity.Salary;
import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface MainPageMapper {

    @Mapping(target = "pieceId", source = "id")
    @Mapping(target = "collectedAt", source = "collectedAt")
    @Mapping(target = "price", source = "item.price")
    @Mapping(target = "rarity", source = "item.rarity")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "image", source = "item.image")
    @Mapping(target = "vendor", source = "item.vendor")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "isMainPiece", source = "main")
    PieceResponse toPieceResponse(Piece piece);

    // User → UserInfo
    @Mapping(target = "amount", source = "salary.amount", defaultValue = "0L")
    @Mapping(target = "amountPerSec", source = "salary.amountPerSec", defaultValue = "0.0")
    @Mapping(target = "payday", source = "salary.payday", defaultValue = "0")
    @Mapping(target = "hasSalary", expression = "java(salary != null)")
    @Mapping(target = "isCheckedIn", source = "user.isCheckedIn")
    @Mapping(target = "isPublic", source = "user.isPublic")
    MainPageResponse.UserInfo toUserInfo(User user, Salary salary);

    // Wish → WishListResponse
    @Mapping(target = "wishId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "isBought", source = "bought")
    @Mapping(target = "isStarred", source = "starred")
    WishListResponse toWishListResponse(Wish wish);
}
