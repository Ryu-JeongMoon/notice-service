package com.example.noticeservice.domain.file.repository;

import com.example.noticeservice.domain.file.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
