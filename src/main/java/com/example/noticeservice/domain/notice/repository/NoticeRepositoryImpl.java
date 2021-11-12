package com.example.noticeservice.domain.notice.repository;

import static com.example.noticeservice.domain.notice.entity.QNotice.notice;

import com.example.noticeservice.domain.notice.entity.Notice;
import com.example.noticeservice.domain.notice.entity.Status;
import com.example.noticeservice.domain.notice.entity.dto.response.NoticeResponse;
import com.example.noticeservice.domain.notice.entity.dto.response.QNoticeResponse;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

// QueryDSL 사용을 위한 Custom Repository, 'Repository 이름 + Impl' 로 네이밍해야 자동 인식됨
@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements CustomNoticeRepository {

    private final JPAQueryFactory queryFactory;

    // 삭제된 게시글은 보이지 않도록 Status 매개변수로 받아 조건에 따라 반환
    @Override
    public List<Notice> findByPageAndStatus(Pageable pageable, Status status) {
        return queryFactory.selectFrom(notice)
            .where(notice.startDateTime.before(LocalDateTime.now()),
                notice.endDateTime.after(LocalDateTime.now()),
                notice.status.eq(status))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    // 페이징 & id 기준 역순 조회, Projection 을 사용하여 NoticeResponse 바로 받기 위함
    // User <-> UserResponse 타입 호환되지 않기 때문에 @AllArgs 와 따로 String username 을 받는 생성자 만든 후
    // 여기서 join fetch 후 user.username 을 생성자에 넣어주고 생성자에서 username 을 받는 UserResponse 객체 생성함
    @Override
    public Page<NoticeResponse> findByStatus(Pageable pageable, Status status) {
        QueryResults<NoticeResponse> results =
            queryFactory
                .select(new QNoticeResponse(
                    notice.id, notice.title, notice.content, notice.createdDateTime, notice.hit, notice.user.username))
                .from(notice)
                .leftJoin(notice.user)
                .where(notice.status.eq(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(notice.id.desc())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
