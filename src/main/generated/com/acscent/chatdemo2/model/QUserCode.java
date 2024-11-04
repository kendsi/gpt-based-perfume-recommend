package com.acscent.chatdemo2.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserCode is a Querydsl query type for UserCode
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserCode extends EntityPathBase<UserCode> {

    private static final long serialVersionUID = 618772342L;

    public static final QUserCode userCode = new QUserCode("userCode");

    public final StringPath code = createString("code");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isEnabled = createBoolean("isEnabled");

    public QUserCode(String variable) {
        super(UserCode.class, forVariable(variable));
    }

    public QUserCode(Path<? extends UserCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserCode(PathMetadata metadata) {
        super(UserCode.class, metadata);
    }

}

