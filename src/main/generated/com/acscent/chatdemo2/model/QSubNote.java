package com.acscent.chatdemo2.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSubNote is a Querydsl query type for SubNote
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSubNote extends EntityPathBase<SubNote> {

    private static final long serialVersionUID = -1563041932L;

    public static final QSubNote subNote = new QSubNote("subNote");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath scent = createString("scent");

    public QSubNote(String variable) {
        super(SubNote.class, forVariable(variable));
    }

    public QSubNote(Path<? extends SubNote> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSubNote(PathMetadata metadata) {
        super(SubNote.class, metadata);
    }

}

