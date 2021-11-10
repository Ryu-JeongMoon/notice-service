package com.example.noticeservice.service;

import com.example.noticeservice.domain.image.entity.Image;
import com.example.noticeservice.util.Messages;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Component
@RequiredArgsConstructor
public class ImageProcessor {

    private static final String ABSOLUTE_PATH = new File("").getAbsolutePath() + File.separator + File.separator;
    private final ImageService imageService;

    public List<Image> parse(List<MultipartFile> files) throws Exception {
        List<Image> imageList = new ArrayList<>();

        if (CollectionUtils.isEmpty(files)) {
            log.warn("FAILED :: There is no files");
            return Collections.emptyList();
        }

        String currentDateString = getCurrentDateString();
        String path = "images" + File.separator + currentDateString;
        File file = new File(path);

        checkFileExistence(file);

        for (MultipartFile multipartFile : files) {
            String currentDateTimeString = getCurrentDateTimeString();

            String contentType = multipartFile.getContentType();
            String originalFilename = multipartFile.getOriginalFilename();
            String originalFileNameWithoutExtension = originalFilename.substring(0, originalFilename.indexOf("."));

            checkFileExtension(multipartFile, contentType);

            String originalFileExtension = getOriginalFileExtension(contentType);
            String fileName = currentDateTimeString + originalFileNameWithoutExtension + originalFileExtension;

            Image image = Image.builder()
                .originalFileName(originalFilename)
                .filePath(path + File.separator + currentDateTimeString)
                .fileSize(multipartFile.getSize())
                .build();

            imageList.add(image);

            file = new File(ABSOLUTE_PATH + path + File.separator + fileName);
            multipartFile.transferTo(file);
            file.setWritable(true);
            file.setReadable(true);
        }

        return imageList;
    }

    private void checkFileExistence(File file) {
        if (!file.exists()) {
            boolean wasSuccessful = file.mkdirs();

            if (!wasSuccessful) {
                log.error("FAILED :: Can't Create a directory");
            }
        }
    }

    private void checkFileExtension(MultipartFile multipartFile, String contentType) {
        if (!StringUtils.hasText(contentType)) {
            log.error("FAILED :: {} has no file extension", multipartFile.getOriginalFilename());
            throw new IllegalArgumentException(Messages.NOT_PROPER_EXTENSION);
        }
    }

    private String getOriginalFileExtension(String contentType) {
        if (contentType.contains("image/jpeg")) {
            return ".jpg";
        } else if (contentType.contains("image/png")) {
            return ".png";
        } else {
            throw new IllegalArgumentException(Messages.NOT_PROPER_EXTENSION);
        }
    }

    private String getCurrentDateString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDateTime.now().format(formatter);
    }

    private String getCurrentDateTimeString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-hhmmss");
        return LocalDateTime.now().format(formatter);
    }
}