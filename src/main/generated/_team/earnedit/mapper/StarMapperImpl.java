package _team.earnedit.mapper;

import _team.earnedit.dto.star.StarListResponse;
import _team.earnedit.entity.Star;
import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-10T00:37:11+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.2.jar, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class StarMapperImpl implements StarMapper {

    @Override
    public StarListResponse toStarListResponse(Star star, Wish wish) {
        if ( star == null && wish == null ) {
            return null;
        }

        StarListResponse.StarListResponseBuilder starListResponse = StarListResponse.builder();

        if ( star != null ) {
            starListResponse.starId( star.getId() );
            starListResponse.userId( starUserId( star ) );
            starListResponse.rank( star.getRank() );
        }
        if ( wish != null ) {
            starListResponse.wishId( wish.getId() );
            starListResponse.name( wish.getName() );
            starListResponse.itemImage( wish.getItemImage() );
            starListResponse.vendor( wish.getVendor() );
            starListResponse.price( wish.getPrice() );
            starListResponse.isBought( wish.isBought() );
            starListResponse.starred( wish.isStarred() );
            starListResponse.url( wish.getUrl() );
            starListResponse.createdAt( wish.getCreatedAt() );
        }

        return starListResponse.build();
    }

    private Long starUserId(Star star) {
        User user = star.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }
}
