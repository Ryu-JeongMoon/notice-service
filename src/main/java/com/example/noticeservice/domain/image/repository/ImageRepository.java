package com.example.noticeservice.domain.image.repository;

import com.example.noticeservice.domain.image.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("select i from Image i join fetch i.notice where i.notice.id = :noticeId")
    List<Image> findByNoticeId(Long noticeId);
}
