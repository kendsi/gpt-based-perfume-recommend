package com.acscent.chatdemo2.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import com.acscent.chatdemo2.exceptions.InvalidLanguageInputException;
import com.acscent.chatdemo2.model.MainNote;
import com.acscent.chatdemo2.model.QMainNote;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MainNoteRepositoryCustomImpl implements MainNoteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MainNote> findByPreferredAndDislikedNotes(List<Integer> preferred, List<Integer> disliked, String language) {
        QMainNote mainNote = QMainNote.mainNote;
        BooleanBuilder builder = new BooleanBuilder();

        switch (language) {
            case "ko":
                builder.and(mainNote.id.between(1, 30));
                break;
            case "en":
                builder.and(mainNote.id.between(31, 60));
                break;
            case "ja":
                builder.and(mainNote.id.between(61, 90));
                break;
            case "zh":
                builder.and(mainNote.id.between(91, 120));
                break;
            default:
                throw new InvalidLanguageInputException("Invalid Language Input: " + language);
        }

        // 선호하는 향 조건 추가 (70 이상)
        if (preferred.contains(1)) builder.and(mainNote.citrus.goe(70));
        if (preferred.contains(2)) builder.and(mainNote.floral.goe(70));
        if (preferred.contains(3)) builder.and(mainNote.woody.goe(70));
        if (preferred.contains(4)) builder.and(mainNote.musk.goe(70));
        if (preferred.contains(5)) builder.and(mainNote.fruity.goe(70));
        if (preferred.contains(6)) builder.and(mainNote.spicy.goe(70));

        // 비선호하는 향 조건 추가 (70 이상 제외)
        if (disliked.contains(1)) builder.and(mainNote.citrus.lt(70));
        if (disliked.contains(2)) builder.and(mainNote.floral.lt(70));
        if (disliked.contains(3)) builder.and(mainNote.woody.lt(70));
        if (disliked.contains(4)) builder.and(mainNote.musk.lt(70));
        if (disliked.contains(5)) builder.and(mainNote.fruity.lt(70));
        if (disliked.contains(6)) builder.and(mainNote.spicy.lt(70));

        return queryFactory.selectFrom(mainNote)
                           .where(builder)
                           .fetch();
    }

    public Optional<MainNote> findByPerfumeNameAndLanguage(String perfumeName, String language) {
        QMainNote mainNote = QMainNote.mainNote;
        BooleanBuilder builder = new BooleanBuilder();

        switch (language) {
            case "ko":
                builder.and(mainNote.id.between(1, 30));
                break;
            case "en":
                builder.and(mainNote.id.between(31, 60));
                break;
            case "ja":
                builder.and(mainNote.id.between(61, 90));
                break;
            case "zh":
                builder.and(mainNote.id.between(91, 120));
                break;
            default:
                throw new InvalidLanguageInputException("Invalid Language Input: " + language);
        }

        builder.and(mainNote.perfumeName.eq(perfumeName));

        return Optional.ofNullable(
            queryFactory.selectFrom(mainNote)
                        .where(builder)
                        .fetchOne()
        );
    }
}
