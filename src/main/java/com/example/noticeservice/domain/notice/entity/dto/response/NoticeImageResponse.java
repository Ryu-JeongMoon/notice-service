package com.example.noticeservice.domain.notice.entity.dto.response;

import com.example.noticeservice.domain.image.entity.Image;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeImageResponse {

    private Long id;

    private String title;

    private String content;

    private LocalDateTime createdDateTime;

    private int hit;

    private String username;

    private List<Image> images;
}
