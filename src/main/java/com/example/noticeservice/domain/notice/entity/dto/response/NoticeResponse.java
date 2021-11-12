package com.example.noticeservice.domain.notice.entity.dto.response;

import com.example.noticeservice.domain.user.entity.dto.response.UserResponse;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private UserResponse userResponse;
}
