package com.example.noticeservice.domain.notice.repository;

import com.example.noticeservice.domain.notice.entity.Notice;
import com.example.noticeservice.domain.notice.entity.Status;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoticeRepository extends JpaRepository<Notice, Long>, CustomNoticeRepository {

    @Query("select n from Notice n join fetch n.user where n.id = :id and n.status = :status")
    Optional<Notice> findByIdAndStatus(Long id, Status status);
}
