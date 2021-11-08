package com.example.noticeservice.domain.notice.entity;

import com.example.noticeservice.domain.file.Files;
import com.example.noticeservice.util.BaseEntity;
import java.time.LocalDateTime;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String content;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private Status status = Status.ACTIVE;

//    @Embedded
    private String files;

    private int hit;

    @Builder
    public Notice(String title, String content, LocalDateTime startDateTime, LocalDateTime endDateTime, String files) {
        this.title = title;
        this.content = content;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.files = files;

        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("공지 시작일시는 종료일시보다 늦을 수 없습니다");
        }
    }

    public void changeStatus() {
        this.status = Status.INACTIVE;
    }
}
