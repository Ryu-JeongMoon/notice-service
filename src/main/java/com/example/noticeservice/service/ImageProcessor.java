package com.example.noticeservice.service;

import com.example.noticeservice.domain.image.entity.Image;
import com.example.noticeservice.util.Messages;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Component
@RequiredArgsConstructor
public class ImageProcessor {

    private final ImageCompressor imageCompressor;

    @Value("${custom.file.path}")
    private String PATH;

    // 파일첨부하지 않고 뷰에서 multipart/form-data 넘기면 files 에 "" 으로 들어와 빈 리스트로 들어오지 않는다
    // TEST 과정에서는 빈 리스트로 들어왔고 누군가 POSTMAN 같이 데이터를 조작해 직접 넘기면 빈 리스트 들어올 수 있기 때문에
    // 빈 리스트인지 확인하는 방어 코드 추가
    public List<Image> parse(List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            log.warn("FAILED :: There is no files");
            return Collections.emptyList();
        }

        List<Image> imageList = new ArrayList<>();

        String currentDateString = getCurrentDateString();
        String path = PATH + File.separator + currentDateString;
        File file = new File(path);

        checkFileExistence(file);

        for (MultipartFile multipartFile : files) {

            String originalFilename = multipartFile.getOriginalFilename();
            if (!StringUtils.hasText(originalFilename)) {
                log.warn("FAILED :: There is no files");
                continue;
            }

            String contentType = multipartFile.getContentType();
            checkFileExtension(multipartFile, contentType);

            assert contentType != null;
            String currentTimeString = getCurrentTimeString();
            String fileNameWithoutExtension = originalFilename.substring(0, originalFilename.indexOf("."));
            String convertedFileExtension = getFileExtension(contentType);
            String fileName = currentTimeString + fileNameWithoutExtension + convertedFileExtension;

            // image 저장 경로 중복을 피하기 위해 절대 경로를 넣지 않고 DB 저장, 이미지 사용 시에 절대 경로 주입 받아 붙여줘야 함
            Image image = Image.builder()
                .originalFileName(originalFilename)
                .fileName(fileName)
                .filePath(File.separator + currentDateString)
                .fileSize(multipartFile.getSize())
                .build();
            imageList.add(image);

            saveFile(path + File.separator + fileName, multipartFile);
        }

        return imageList;
    }

    private void saveFile(String pathname, MultipartFile multipartFile) {
        CompletableFuture.runAsync(() -> {
            try {
                imageCompressor.compressAndSave(multipartFile.getBytes(), pathname);
                File file = new File(pathname);
                file.setWritable(true);
                file.setReadable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, new ScheduledThreadPoolExecutor(12));
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

    private String getFileExtension(String contentType) {
        if (contentType.contains("image/jpeg")) {
            return ".jpg";
        } else if (contentType.contains("image/png")) {
            return ".png";
        } else {
            log.error("FAILED :: {} is unsupported file extension", contentType);
            throw new IllegalArgumentException(Messages.NOT_PROPER_EXTENSION);
        }
    }

    // 이미지 일별 관리를 위해 이미지 저장 날짜에 해당하는 폴더 생성을 위한 날짜 문자열
    private String getCurrentDateString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDateTime.now().format(formatter);
    }

    // 이미지 이름 중복 방지를 위해 이미지 저장 날짜 + 시간에 해당하는 이미지 이름 문자열
    private String getCurrentTimeString() {
        return String.valueOf(System.nanoTime());
    }
}