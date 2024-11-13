package com.acscent.chatdemo2.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import com.acscent.chatdemo2.model.MainNote;
import com.acscent.chatdemo2.model.QMainNote;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MainNoteRepositoryCustomImpl implements MainNoteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MainNote> findByPreferredAndDislikedNotes(List<String> preferred, List<String> disliked) {
        QMainNote mainNote = QMainNote.mainNote;
        BooleanBuilder builder = new BooleanBuilder();

        log.info(preferred.toString());
        log.info(disliked.toString());

        // 선호하는 향 조건 추가 (6 이상)
        if (preferred.contains("citrus")) builder.and(mainNote.citrus.goe(60));
        if (preferred.contains("floral")) builder.and(mainNote.floral.goe(60));
        if (preferred.contains("woody")) builder.and(mainNote.woody.goe(60));
        if (preferred.contains("musk")) builder.and(mainNote.musk.goe(60));
        if (preferred.contains("fruity")) builder.and(mainNote.fruity.goe(60));
        if (preferred.contains("spicy")) builder.and(mainNote.spicy.goe(60));

        // 비선호하는 향 조건 추가 (4 이하)
        if (disliked.contains("citrus")) builder.and(mainNote.citrus.loe(40));
        if (disliked.contains("floral")) builder.and(mainNote.floral.loe(40));
        if (disliked.contains("woody")) builder.and(mainNote.woody.loe(40));
        if (disliked.contains("musk")) builder.and(mainNote.musk.loe(40));
        if (disliked.contains("fruity")) builder.and(mainNote.fruity.loe(40));
        if (disliked.contains("spicy")) builder.and(mainNote.spicy.loe(40));

        return queryFactory.selectFrom(mainNote)
                           .where(builder)
                           .fetch();
    }
}
