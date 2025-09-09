package _team.earnedit.mapper;

import _team.earnedit.dto.wish.WishAddRequest;
import _team.earnedit.dto.wish.WishAddResponse;
import _team.earnedit.dto.wish.WishDetailResponse;
import _team.earnedit.dto.wish.WishListResponse;
import _team.earnedit.dto.wish.WishUpdateResponse;
import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-10T00:37:12+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.2.jar, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class WishMapperImpl implements WishMapper {

    @Override
    public Wish toEntity(WishAddRequest request, User user, String imageUrl, boolean isStarred) {
        if ( request == null ) {
            return null;
        }

        Wish.WishBuilder wish = Wish.builder();

        wish.name( request.getName() );
        wish.price( request.getPrice() );
        wish.vendor( request.getVendor() );
        wish.url( request.getUrl() );

        wish.user( user );
        wish.itemImage( imageUrl );
        wish.isStarred( isStarred );

        return wish.build();
    }

    @Override
    public WishAddResponse toResponse(Wish wish) {
        if ( wish == null ) {
            return null;
        }

        WishAddResponse.WishAddResponseBuilder wishAddResponse = WishAddResponse.builder();

        if ( wish.getId() != null ) {
            wishAddResponse.wishId( wish.getId() );
        }
        wishAddResponse.createdAt( wish.getCreatedAt() );

        return wishAddResponse.build();
    }

    @Override
    public WishListResponse toWishListResponse(Wish wish) {
        if ( wish == null ) {
            return null;
        }

        WishListResponse.WishListResponseBuilder wishListResponse = WishListResponse.builder();

        wishListResponse.wishId( wish.getId() );
        wishListResponse.userId( wishUserId( wish ) );
        wishListResponse.name( wish.getName() );
        wishListResponse.price( wish.getPrice() );
        wishListResponse.itemImage( wish.getItemImage() );
        wishListResponse.isBought( wish.isBought() );
        wishListResponse.vendor( wish.getVendor() );
        wishListResponse.createdAt( wish.getCreatedAt() );
        wishListResponse.isStarred( wish.isStarred() );
        wishListResponse.url( wish.getUrl() );

        return wishListResponse.build();
    }

    @Override
    public List<WishListResponse> toWishListResponseList(List<Wish> wishes) {
        if ( wishes == null ) {
            return null;
        }

        List<WishListResponse> list = new ArrayList<WishListResponse>( wishes.size() );
        for ( Wish wish : wishes ) {
            list.add( toWishListResponse( wish ) );
        }

        return list;
    }

    @Override
    public WishUpdateResponse toWishUpdateResponse(Wish wish) {
        if ( wish == null ) {
            return null;
        }

        WishUpdateResponse.WishUpdateResponseBuilder wishUpdateResponse = WishUpdateResponse.builder();

        if ( wish.getId() != null ) {
            wishUpdateResponse.wishId( wish.getId() );
        }
        wishUpdateResponse.name( wish.getName() );
        wishUpdateResponse.vendor( wish.getVendor() );
        wishUpdateResponse.price( wish.getPrice() );
        wishUpdateResponse.itemImage( wish.getItemImage() );
        wishUpdateResponse.url( wish.getUrl() );
        wishUpdateResponse.updatedAt( wish.getUpdatedAt() );

        return wishUpdateResponse.build();
    }

    @Override
    public WishDetailResponse toWishDetailResponse(Wish wish) {
        if ( wish == null ) {
            return null;
        }

        WishDetailResponse.WishDetailResponseBuilder wishDetailResponse = WishDetailResponse.builder();

        wishDetailResponse.userId( wishUserId( wish ) );
        wishDetailResponse.wishId( wish.getId() );
        wishDetailResponse.isStarred( wish.isStarred() );
        wishDetailResponse.isBought( wish.isBought() );
        wishDetailResponse.name( wish.getName() );
        wishDetailResponse.price( wish.getPrice() );
        wishDetailResponse.itemImage( wish.getItemImage() );
        wishDetailResponse.vendor( wish.getVendor() );
        wishDetailResponse.createdAt( wish.getCreatedAt() );
        wishDetailResponse.updatedAt( wish.getUpdatedAt() );
        wishDetailResponse.url( wish.getUrl() );

        return wishDetailResponse.build();
    }

    private Long wishUserId(Wish wish) {
        User user = wish.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }
}
