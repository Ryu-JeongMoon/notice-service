package com.example.noticeservice.domain.notice.entity;

import com.example.noticeservice.domain.image.entity.Image;
import com.example.noticeservice.domain.user.entity.User;
import com.example.noticeservice.util.BaseEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "notice_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();

    private int hit;

    @Builder
    public Notice(String title, String content, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.title = title;
        this.content = content;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;

        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("공지 시작일시는 종료일시보다 늦을 수 없습니다");
        }
    }

    public void addImage(Image image) {
        images.add(image);
        image.setNotice(this);
    }

    public void addImages(List<Image> images) {
        this.images.addAll(images);
        images.forEach(i -> i.setNotice(this));
    }

    public void changeStatus() {
        this.status = Status.INACTIVE;
    }

    public void increaseHit() {
        this.hit++;
    }
}
