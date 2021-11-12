package com.example.noticeservice.controller.api;

import com.example.noticeservice.domain.image.entity.dto.response.ImageResponse;
import com.example.noticeservice.service.ImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{noticeId}")
    public ResponseEntity getImages(@PathVariable Long noticeId) {
        List<ImageResponse> imageResponses = imageService.findByNoticeId(noticeId);
        CollectionModel<ImageResponse> collectionModel = CollectionModel.of(imageResponses);
        return ResponseEntity.ok(collectionModel);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity deleteImage(@PathVariable Long imageId) {
        imageService.delete(imageId);
        return ResponseEntity.noContent().build();
    }
}
