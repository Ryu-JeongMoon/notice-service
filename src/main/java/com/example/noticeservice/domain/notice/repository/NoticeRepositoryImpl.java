package com.example.noticeservice.domain.notice.repository;

import static com.example.noticeservice.domain.notice.entity.QNotice.notice;

import com.example.noticeservice.domain.notice.entity.Notice;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements CustomNoticeRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Notice> findByPage(Pageable pageable) {
        return queryFactory.selectFrom(notice)
            .where(notice.startDateTime.before(LocalDateTime.now()), notice.endDateTime.after(LocalDateTime.now()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
}
