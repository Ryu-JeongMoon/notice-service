package com.example.noticeservice.domain.notice.repository;

import com.example.noticeservice.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long>, CustomNoticeRepository {


}
