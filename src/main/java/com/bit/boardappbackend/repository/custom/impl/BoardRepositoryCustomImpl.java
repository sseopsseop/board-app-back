package com.bit.boardappbackend.repository.custom.impl;

import com.bit.boardappbackend.entity.Board;
import com.bit.boardappbackend.repository.custom.BoardRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bit.boardappbackend.entity.QBoard.board;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Board> searchAll(String searchCondition, String searchKeyword, Pageable pageable) {
        List<Board> boardList = jpaQueryFactory.selectFrom(board)
                .where(getSearch(searchCondition, searchKeyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(board.count())
                .from(board)
                .where(getSearch(searchCondition, searchKeyword))
                .fetchOne();

        return new PageImpl<>(boardList, pageable, total);
    }

    public BooleanBuilder getSearch(String searchCondition, String searchKeyword) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if(searchKeyword == null || searchKeyword.isEmpty()) {
            return null;
        }

        if(searchCondition.equalsIgnoreCase("all")) {
            booleanBuilder.or(board.title.containsIgnoreCase(searchKeyword));
            booleanBuilder.or(board.content.containsIgnoreCase(searchKeyword));
            booleanBuilder.or(board.member.nickname.containsIgnoreCase(searchKeyword));
        } else if(searchCondition.equalsIgnoreCase("title")) {
            booleanBuilder.and(board.title.containsIgnoreCase(searchKeyword));
        } else if(searchCondition.equalsIgnoreCase("content")) {
            booleanBuilder.and(board.content.containsIgnoreCase(searchKeyword));
        } else if(searchCondition.equalsIgnoreCase("writer")) {
            booleanBuilder.and(board.member.nickname.containsIgnoreCase(searchKeyword));
        }

        return booleanBuilder;
    }
}
