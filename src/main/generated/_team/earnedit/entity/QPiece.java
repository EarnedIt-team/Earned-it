package _team.earnedit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPiece is a Querydsl query type for Piece
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPiece extends EntityPathBase<Piece> {

    private static final long serialVersionUID = 893519965L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPiece piece = new QPiece("piece");

    public final DateTimePath<java.time.LocalDateTime> collectedAt = createDateTime("collectedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isCollected = createBoolean("isCollected");

    public final BooleanPath isMain = createBoolean("isMain");

    public final QItem item;

    public final QUser user;

    public QPiece(String variable) {
        this(Piece.class, forVariable(variable), INITS);
    }

    public QPiece(Path<? extends Piece> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPiece(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPiece(PathMetadata metadata, PathInits inits) {
        this(Piece.class, metadata, inits);
    }

    public QPiece(Class<? extends Piece> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new QItem(forProperty("item")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

