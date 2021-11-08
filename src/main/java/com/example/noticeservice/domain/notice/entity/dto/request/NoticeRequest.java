package com.example.noticeservice.domain.notice.entity.dto.request;


import com.example.noticeservice.domain.file.Files;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private String files;
}
