package com.example.noticeservice.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomPage {

    private Integer size;
    private Integer totalElements;
    private Integer totalPages;
    private Integer number;
}
