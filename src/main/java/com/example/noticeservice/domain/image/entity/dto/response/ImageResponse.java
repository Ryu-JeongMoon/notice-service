package com.example.noticeservice.domain.image.entity.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class ImageResponse {

    private String origFileName;
    private String filePath;
    private Long fileSize;

    @Builder
    public ImageResponse(String origFileName, String filePath, Long fileSize) {
        this.origFileName = origFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }
}
