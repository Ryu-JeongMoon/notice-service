package com.example.noticeservice.domain.notice.repository;

import com.example.noticeservice.domain.notice.entity.Notice;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomNoticeRepository {

    List<Notice> findByPage(Pageable pageable);
}
