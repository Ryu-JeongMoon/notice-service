package com.example.noticeservice.domain.notice.repository;

import com.example.noticeservice.domain.notice.entity.Notice;
import com.example.noticeservice.domain.notice.entity.Status;
import java.util.List;
import org.springframework.data.domain.Pageable;

// QueryDSL 사용을 위한 Custom Interface
public interface CustomNoticeRepository {

    List<Notice> findByPageAndStatus(Pageable pageable, Status status);
}
