package _team.earnedit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPuzzleSlot is a Querydsl query type for PuzzleSlot
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPuzzleSlot extends EntityPathBase<PuzzleSlot> {

    private static final long serialVersionUID = 1417375565L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPuzzleSlot puzzleSlot = new QPuzzleSlot("puzzleSlot");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QItem item;

    public final NumberPath<Integer> slotIndex = createNumber("slotIndex", Integer.class);

    public final EnumPath<Theme> theme = createEnum("theme", Theme.class);

    public QPuzzleSlot(String variable) {
        this(PuzzleSlot.class, forVariable(variable), INITS);
    }

    public QPuzzleSlot(Path<? extends PuzzleSlot> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPuzzleSlot(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPuzzleSlot(PathMetadata metadata, PathInits inits) {
        this(PuzzleSlot.class, metadata, inits);
    }

    public QPuzzleSlot(Class<? extends PuzzleSlot> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new QItem(forProperty("item")) : null;
    }

}

