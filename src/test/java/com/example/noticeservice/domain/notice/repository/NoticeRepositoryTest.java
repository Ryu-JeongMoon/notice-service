package com.example.noticeservice.domain.notice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.noticeservice.config.JpaTestConfig;
import com.example.noticeservice.domain.image.entity.Image;
import com.example.noticeservice.domain.image.repository.ImageRepository;
import com.example.noticeservice.domain.notice.entity.Notice;
import com.example.noticeservice.domain.notice.entity.Status;
import com.example.noticeservice.domain.user.entity.User;
import com.example.noticeservice.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@DataJpaTest
@Import(JpaTestConfig.class)
class NoticeRepositoryTest {

    private static Long NOTICE_ID_1;
    private static Long NOTICE_ID_2;
    private final String TITLE = "title";
    private final String CONTENT = "content";

    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    NoticeRepository noticeRepository;

    @BeforeEach
    void init() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = User.builder()
            .username("user")
            .password(passwordEncoder.encode("password"))
            .build();
        userRepository.save(user);

        Notice n1 = Notice.builder()
            .title(TITLE)
            .content(CONTENT)
            .user(user)
            .startDateTime(LocalDateTime.now())
            .endDateTime(LocalDateTime.of(2044, 11, 4, 3, 1, 2))
            .build();
        NOTICE_ID_1 = noticeRepository.save(n1).getId();

        Notice n2 = Notice.builder()
            .title(TITLE)
            .content(CONTENT)
            .user(user)
            .startDateTime(LocalDateTime.now())
            .endDateTime(LocalDateTime.of(2044, 11, 4, 3, 1, 2))
            .build();

        Image i1 = Image.builder()
            .filePath("12345")
            .fileSize(34343L)
            .originalFileName("panda")
            .build();

        Image i2 = Image.builder()
            .filePath("12345")
            .fileSize(34343L)
            .originalFileName("panda")
            .build();

        Image i3 = Image.builder()
            .filePath("12345")
            .fileSize(34343L)
            .originalFileName("panda")
            .build();

        n2.addImage(i1);
        n2.addImage(i2);
        n2.addImage(i3);

        NOTICE_ID_2 = noticeRepository.save(n2).getId();
        imageRepository.save(i1);
        imageRepository.save(i2);
        imageRepository.save(i3);
    }

    @Test
    @DisplayName("게시글 기본 상태 ACTIVE 로 추가된다")
    void findByIdAndStatus() {
        Notice notice = noticeRepository.findByIdAndStatus(NOTICE_ID_1, Status.ACTIVE).get();
        assertThat(notice.getId()).isNotNull();
    }

    @Test
    @DisplayName("join fetch 하지 않더라도 쿼리 한번 나간다")
    void findByIdAndStatusWithImages() {
        Notice notice = noticeRepository.findByIdAndStatus(NOTICE_ID_2, Status.ACTIVE).get();
        List<Image> images = notice.getImages();

        for (Image image : images) {
            System.out.println("image = " + image.getId());
        }

        assertThat(notice.getId()).isNotNull();
    }
}