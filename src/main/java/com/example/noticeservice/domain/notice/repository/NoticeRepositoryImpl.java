package com.example.noticeservice.domain.notice.repository;

import static com.example.noticeservice.domain.notice.entity.QNotice.notice;

import com.example.noticeservice.domain.notice.entity.Notice;
import com.example.noticeservice.domain.notice.entity.Status;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
}
