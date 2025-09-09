package _team.earnedit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QItem is a Querydsl query type for Item
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItem extends EntityPathBase<Item> {

    private static final long serialVersionUID = -1495395388L;

    public static final QItem item = new QItem("item");

    public final StringPath category = createString("category");

    public final StringPath category2 = createString("category2");

    public final StringPath category3 = createString("category3");

    public final StringPath category4 = createString("category4");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    public final StringPath maker = createString("maker");

    public final StringPath name = createString("name");

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final StringPath productId = createString("productId");

    public final StringPath productType = createString("productType");

    public final EnumPath<Rarity> rarity = createEnum("rarity", Rarity.class);

    public final StringPath url = createString("url");

    public final StringPath vendor = createString("vendor");

    public QItem(String variable) {
        super(Item.class, forVariable(variable));
    }

    public QItem(Path<? extends Item> path) {
        super(path.getType(), path.getMetadata());
    }

    public QItem(PathMetadata metadata) {
        super(Item.class, metadata);
    }

}

