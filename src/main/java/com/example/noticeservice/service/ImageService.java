package com.example.noticeservice.service;

import com.example.noticeservice.domain.image.entity.dto.response.ImageResponse;
import com.example.noticeservice.domain.image.mapper.ImageResponseMapper;
import com.example.noticeservice.domain.image.repository.ImageRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageResponseMapper responseMapper;

    @Transactional(readOnly = true)
    public List<ImageResponse> findByNoticeId(Long noticeId) {
        return imageRepository.findByNoticeId(noticeId)
            .stream()
            .map(responseMapper::toDto)
            .collect(Collectors.toList());
    }

    public void delete(Long imageId) {
        imageRepository.deleteById(imageId);
    }
}
