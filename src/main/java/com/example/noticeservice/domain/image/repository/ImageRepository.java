package com.example.noticeservice.domain.image.repository;

import com.example.noticeservice.domain.image.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByNoticeId(Long noticeId);
}
