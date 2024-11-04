package com.acscent.chatdemo2.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMainNote is a Querydsl query type for MainNote
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMainNote extends EntityPathBase<MainNote> {

    private static final long serialVersionUID = 877145193L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMainNote mainNote = new QMainNote("mainNote");

    public final QSubNote baseNote;

    public final NumberPath<Integer> citrus = createNumber("citrus", Integer.class);

    public final StringPath description = createString("description");

    public final NumberPath<Integer> floral = createNumber("floral", Integer.class);

    public final NumberPath<Integer> fruity = createNumber("fruity", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSubNote middleNote;

    public final StringPath name = createString("name");

    public final StringPath perfumeName = createString("perfumeName");

    public final StringPath recommendation = createString("recommendation");

    public final StringPath scent = createString("scent");

    public final NumberPath<Integer> spicy = createNumber("spicy", Integer.class);

    public final NumberPath<Integer> watery = createNumber("watery", Integer.class);

    public final NumberPath<Integer> woody = createNumber("woody", Integer.class);

    public QMainNote(String variable) {
        this(MainNote.class, forVariable(variable), INITS);
    }

    public QMainNote(Path<? extends MainNote> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMainNote(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMainNote(PathMetadata metadata, PathInits inits) {
        this(MainNote.class, metadata, inits);
    }

    public QMainNote(Class<? extends MainNote> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.baseNote = inits.isInitialized("baseNote") ? new QSubNote(forProperty("baseNote")) : null;
        this.middleNote = inits.isInitialized("middleNote") ? new QSubNote(forProperty("middleNote")) : null;
    }

}

