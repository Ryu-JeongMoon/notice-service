package com.example.noticeservice.domain.notice.entity.dto.response;

import java.time.LocalDateTime;
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
public class NoticeResponse {

    private Long id;

    private String title;

    private String content;

    private LocalDateTime createdDateTime;

    private int hit;

    private String username;

    private List<MultipartFile> files;
}
