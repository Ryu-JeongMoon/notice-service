package com.example.noticeservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.noticeservice.domain.notice.entity.Notice;
import com.example.noticeservice.domain.notice.entity.dto.request.NoticeRequest;
import com.example.noticeservice.domain.notice.entity.dto.response.NoticeResponse;
import com.example.noticeservice.domain.notice.repository.NoticeRepository;
import com.example.noticeservice.domain.user.entity.User;
import com.example.noticeservice.domain.user.entity.dto.request.UserRequest;
import com.example.noticeservice.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class NoticeServiceTest {

    private static Long NOTICE_ID;
    private final String TITLE = "panda";
    private final String CONTENT = "bear";
    private final String USERNAME = "panda";
    private final String PASSWORD = "bear";

    private final PageRequest pageRequest = PageRequest.of(0, 10);

    @Autowired
    UserService userService;
    @Autowired
    NoticeService noticeService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    NoticeRepository noticeRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void init() throws Exception {
        User user = User.builder()
            .username(USERNAME)
            .password(passwordEncoder.encode(PASSWORD))
            .build();
        userRepository.save(user);

        Notice notice = Notice.builder()
            .title(TITLE)
            .content(CONTENT)
            .user(user)
            .startDateTime(LocalDateTime.now())
            .endDateTime(LocalDateTime.of(2022, 12, 31, 3, 2, 4))
            .build();
        NOTICE_ID = noticeRepository.save(notice).getId();
    }

    @Test
    @DisplayName("게시글 다건 조회")
    void getNotices() {
        List<NoticeResponse> notices = noticeService.getNotices(pageRequest);
        assertThat(notices.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 단건 조회")
    void getNotice() {
        NoticeResponse noticeResponse = noticeService.getNotice(NOTICE_ID);
        assertThat(noticeResponse.getTitle()).isEqualTo(TITLE);
        assertThat(noticeResponse.getContent()).isEqualTo(CONTENT);
    }

    // 게시글 추가 과정에는 실제 USER 데이터가 필요하므로 @WithMockUser 사용 불가
    // 회원 추가, 로그인 과정 선행
    @Test
    @DisplayName("게시글 추가")
    void create() throws Exception {
        UserRequest userRequest = UserRequest.builder()
            .username("USER")
            .password("PASSWORD")
            .build();
        userService.register(userRequest);
        userService.login(userRequest);

        NoticeRequest noticeRequest = NoticeRequest.builder()
            .title("title")
            .content("content")
            .startDateTime(LocalDateTime.now())
            .endDateTime(LocalDateTime.of(2031, 12, 22, 3, 4, 5))
            .build();
        noticeService.create(noticeRequest, Collections.emptyList());

        List<NoticeResponse> notices = noticeService.getNotices(pageRequest);
        assertThat(notices.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("게시글 수정")
    void edit() throws Exception {
        NoticeRequest noticeRequest = NoticeRequest.builder()
            .title("title")
            .content("content")
            .startDateTime(LocalDateTime.now())
            .endDateTime(LocalDateTime.of(2031, 12, 22, 3, 4, 5))
            .build();

        noticeService.edit(NOTICE_ID, noticeRequest, Collections.emptyList());

        NoticeResponse noticeResponse = noticeService.getNotice(NOTICE_ID);
        assertThat(noticeResponse.getTitle()).isEqualTo("title");
    }

    @Test
    @DisplayName("게시글 삭제")
    void delete() {
        noticeService.delete(NOTICE_ID);
        List<NoticeResponse> notices = noticeService.getNotices(pageRequest);
        assertThat(notices.size()).isEqualTo(0);
    }
}