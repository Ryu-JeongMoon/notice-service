package com.example.noticeservice.domain.image.entity;

import com.example.noticeservice.domain.notice.entity.Notice;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    private String originalFileName;

    private String fileName;

    private String filePath;

    private Long fileSize;

    @Builder
    public Image(String originalFileName, String fileName, String filePath, Long fileSize) {
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    // 연관 관계 메서드
    public void setNotice(Notice notice) {
        this.notice = notice;

        if (!notice.getImages().contains(this)) {
            notice.getImages().add(this);
        }
    }
}

// originalFileName 을 넣어줘야 하는 것인가?! fileName 만 있어도 충분할 것 같은데
// 이미지 수정을 위해서는 기존 파일 이름이 있어야 가능하므로 저장!