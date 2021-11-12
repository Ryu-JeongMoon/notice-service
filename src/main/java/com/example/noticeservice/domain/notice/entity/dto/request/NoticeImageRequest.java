package com.example.noticeservice.domain.notice.entity.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.persistence.Lob;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

// multipart/form-data 로 넘어오는 경우 String 으로 binding, 따라서 String -> LocalDateTime 변환 필요
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeImageRequest {

    private String title;

    @Lob
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private String startDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
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

    public NoticeRequest toEditNoticeRequest() {
        return NoticeRequest.builder()
            .title(title)
            .content(content)
            .build();
    }
}
