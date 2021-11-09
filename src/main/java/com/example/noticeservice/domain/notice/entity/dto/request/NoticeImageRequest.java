package com.example.noticeservice.domain.notice.entity.dto.request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeImageRequest {

    private String title;

    private String content;

    private String startDateTime;

    private String endDateTime;

    private List<MultipartFile> files;

    public NoticeRequest toNoticeRequest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        return NoticeRequest.builder()
            .title(title)
            .content(content)
            .startDateTime(LocalDateTime.parse(startDateTime, formatter))
            .endDateTime(LocalDateTime.parse(endDateTime, formatter))
            .build();
    }
}
