package com.example.noticeservice.domain.file.entity;

import com.example.noticeservice.domain.notice.entity.Notice;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
    @GeneratedValue
    @Column(name = "image_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String filePath;

    private Long fileSize;

    @Builder
    public Image(String originalFileName, String filePath, Long fileSize) {
        this.originalFileName = originalFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;

        if (!notice.getImages().contains(this)) {
            notice.getImages().add(this);
        }
    }
}
