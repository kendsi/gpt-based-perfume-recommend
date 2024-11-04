package com.acscent.chatdemo2.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPerfume is a Querydsl query type for Perfume
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPerfume extends EntityPathBase<Perfume> {

    private static final long serialVersionUID = -373155226L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPerfume perfume = new QPerfume("perfume");

    public final com.acscent.chatdemo2.data.QAppearance appearance;

    public final StringPath code = createString("code");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageName = createString("imageName");

    public final QMainNote mainNote;

    public final StringPath perfumeName = createString("perfumeName");

    public final StringPath profile = createString("profile");

    public final StringPath userName = createString("userName");

    public QPerfume(String variable) {
        this(Perfume.class, forVariable(variable), INITS);
    }

    public QPerfume(Path<? extends Perfume> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPerfume(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPerfume(PathMetadata metadata, PathInits inits) {
        this(Perfume.class, metadata, inits);
    }

    public QPerfume(Class<? extends Perfume> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.appearance = inits.isInitialized("appearance") ? new com.acscent.chatdemo2.data.QAppearance(forProperty("appearance")) : null;
        this.mainNote = inits.isInitialized("mainNote") ? new QMainNote(forProperty("mainNote"), inits.get("mainNote")) : null;
    }

}

