package com.example.noticeservice.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.noticeservice.config.JpaConfigTest;
import com.example.noticeservice.domain.notice.entity.Notice;
import com.example.noticeservice.domain.notice.repository.NoticeRepository;
import java.time.LocalDateTime;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Log4j2
@DataJpaTest
@Import(JpaConfigTest.class)
class BaseEntityTest {

    @Autowired
    NoticeRepository noticeRepository;

    @BeforeEach
    void init() {
        noticeRepository.deleteAll();
    }

    @Test
    @DisplayName("JPA Auditing 작동 - CreatedDateTime")
    void getCreatedDateTime() {
        Notice notice = Notice.builder()
            .title("title")
            .content("content")
            .startDateTime(LocalDateTime.now())
            .endDateTime(LocalDateTime.of(2022, 12, 31, 0, 5, 5))
            .build();
        Notice result = noticeRepository.save(notice);

        assertThat(result.getCreatedDateTime()).isNotNull();
    }

    @Test
    @DisplayName("JPA Auditing 작동 - LastModifiedDateTime")
    void getLastModifiedDateTime() {
        Notice notice = Notice.builder()
            .title("title")
            .content("content")
            .startDateTime(LocalDateTime.now())
            .endDateTime(LocalDateTime.of(2022, 12, 31, 0, 5, 5))
            .build();
        Notice result = noticeRepository.save(notice);

        assertThat(result.getLastModifiedDateTime()).isNotNull();
    }
}