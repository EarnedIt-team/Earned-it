package _team.earnedit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReportReason is a Querydsl query type for ReportReason
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReportReason extends EntityPathBase<ReportReason> {

    private static final long serialVersionUID = -2125695927L;

    public static final QReportReason reportReason = new QReportReason("reportReason");

    public final StringPath code = createString("code");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QReportReason(String variable) {
        super(ReportReason.class, forVariable(variable));
    }

    public QReportReason(Path<? extends ReportReason> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReportReason(PathMetadata metadata) {
        super(ReportReason.class, metadata);
    }

}

