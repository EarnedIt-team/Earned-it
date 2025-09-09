package _team.earnedit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSalary is a Querydsl query type for Salary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSalary extends EntityPathBase<Salary> {

    private static final long serialVersionUID = 2008021563L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSalary salary = new QSalary("salary");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final NumberPath<Double> amountPerSec = createNumber("amountPerSec", Double.class);

    public final NumberPath<Integer> dependentCount = createNumber("dependentCount", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> payday = createNumber("payday", Integer.class);

    public final BooleanPath severance = createBoolean("severance");

    public final BooleanPath tax = createBoolean("tax");

    public final NumberPath<Long> taxExemptAmount = createNumber("taxExemptAmount", Long.class);

    public final EnumPath<Salary.SalaryType> type = createEnum("type", Salary.SalaryType.class);

    public final QUser user;

    public QSalary(String variable) {
        this(Salary.class, forVariable(variable), INITS);
    }

    public QSalary(Path<? extends Salary> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSalary(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSalary(PathMetadata metadata, PathInits inits) {
        this(Salary.class, metadata, inits);
    }

    public QSalary(Class<? extends Salary> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

