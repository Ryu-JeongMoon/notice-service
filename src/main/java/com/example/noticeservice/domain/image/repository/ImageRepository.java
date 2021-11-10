package com.example.noticeservice.domain.image.repository;

import com.example.noticeservice.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
