package _team.earnedit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEmailToken is a Querydsl query type for EmailToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmailToken extends EntityPathBase<EmailToken> {

    private static final long serialVersionUID = -1979322450L;

    public static final QEmailToken emailToken = new QEmailToken("emailToken");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final DateTimePath<java.time.LocalDateTime> expiredAt = createDateTime("expiredAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isVerified = createBoolean("isVerified");

    public final StringPath token = createString("token");

    public QEmailToken(String variable) {
        super(EmailToken.class, forVariable(variable));
    }

    public QEmailToken(Path<? extends EmailToken> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmailToken(PathMetadata metadata) {
        super(EmailToken.class, metadata);
    }

}

