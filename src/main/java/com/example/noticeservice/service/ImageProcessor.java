package com.example.noticeservice.service;

import com.example.noticeservice.domain.file.entity.Image;
import com.example.noticeservice.util.Messages;
import java.io.File;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    private final ImageService imageService;
    private static final String ABSOLUTE_PATH = new File("").getAbsolutePath() + File.separator + File.separator;

    public List<Image> parse(List<MultipartFile> files) throws Exception {
        List<Image> imageList = new ArrayList<>();

        if (CollectionUtils.isEmpty(files)) {
            throw new IllegalArgumentException();
        }

        String currentDateTimeString = getCurrentDateTimeString();
        String path = "images" + File.separator + currentDateTimeString;
        File file = new File(path);

        checkFileExistence(file);

        for (MultipartFile multipartFile : files) {
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

    private String getCurrentDateTimeString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd-hhmmss");
        return LocalDateTime.now().format(formatter);
    }
}