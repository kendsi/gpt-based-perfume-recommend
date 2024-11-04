package com.acscent.chatdemo2.data;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAppearance is a Querydsl query type for Appearance
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QAppearance extends BeanPath<Appearance> {

    private static final long serialVersionUID = 555304591L;

    public static final QAppearance appearance = new QAppearance("appearance");

    public final StringPath facialFeature = createString("facialFeature");

    public final StringPath style = createString("style");

    public final StringPath vibe = createString("vibe");

    public QAppearance(String variable) {
        super(Appearance.class, forVariable(variable));
    }

    public QAppearance(Path<? extends Appearance> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAppearance(PathMetadata metadata) {
        super(Appearance.class, metadata);
    }

}

