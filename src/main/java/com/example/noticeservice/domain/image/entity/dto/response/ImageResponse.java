package com.example.noticeservice.domain.image.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {

    private String origFileName;
    private String fileName;
    private String filePath;
    private Long fileSize;
}
