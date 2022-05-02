package com.example.noticeservice.service;

import com.example.noticeservice.domain.image.entity.Image;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class ImageProcessorTest {

    private final ImageCompressor imageCompressor = new ImageCompressor();
    private final ImageProcessor imageProcessor = new ImageProcessor(imageCompressor);

    @Test
    @DisplayName("파일 없을 시 빈 리스트 반환")
    void parseEmpty() throws Exception {
        List<Image> images = imageProcessor.parse(Collections.emptyList());
        Assertions.assertThat(images.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("파일 형식 jpg/png/jpeg 맞지 않을 시 빈 리스트 반환")
    void parseNotProperImage() throws Exception {
        List<MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("text.text", "".getBytes(StandardCharsets.UTF_8)));

        List<Image> images = imageProcessor.parse(files);
        Assertions.assertThat(images.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("파일 형식 맞는 것과 맞지 않는 것 들어올 때 올바른 파일만 진행")
    void parseMultiImage() throws Exception {
        List<MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("text.text", "".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("text.jpg", "text.jpg", "image/jpeg",
            "".getBytes(StandardCharsets.UTF_8)));

        List<Image> images = imageProcessor.parse(files);
        Assertions.assertThat(images.size()).isEqualTo(1);
    }
}