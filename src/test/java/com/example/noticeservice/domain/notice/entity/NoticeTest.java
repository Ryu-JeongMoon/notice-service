package com.example.noticeservice.domain.notice.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NoticeTest {

    private Notice notice;

    @BeforeEach
    void init() {
        notice = Notice.builder()
            .title("제목")
            .content("내용")
            .startDateTime(LocalDateTime.now())
            .endDateTime(LocalDateTime.of(2021, 11, 30, 0, 0, 0))
            .build();
    }

    @Test
    @DisplayName("조회수 기본 값 0")
    void hitTest() {
        assertThat(notice.getHit()).isEqualTo(0);
    }

    @Test
    @DisplayName("상태 기본 값 ACTIVE")
    void statusTest() {
        assertThat(notice.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    @DisplayName("시작일시는 종료일시보다 늦을 시 IllegalArgumentException 발생")
    void dateTest() {
        assertThrows(IllegalArgumentException.class, () -> Notice.builder()
                .startDateTime(LocalDateTime.of(2021, 11, 30, 0, 0, 1))
                .endDateTime(LocalDateTime.of(2021, 11, 30, 0, 0, 0))
                .build());
    }
}