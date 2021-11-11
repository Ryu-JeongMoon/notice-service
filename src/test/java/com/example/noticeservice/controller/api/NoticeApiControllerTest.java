package com.example.noticeservice.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.noticeservice.domain.notice.entity.Notice;
import com.example.noticeservice.domain.notice.entity.dto.request.NoticeImageRequest;
import com.example.noticeservice.domain.notice.entity.dto.request.NoticeRequest;
import com.example.noticeservice.domain.notice.repository.NoticeRepository;
import com.example.noticeservice.domain.user.entity.User;
import com.example.noticeservice.domain.user.repository.UserRepository;
import com.example.noticeservice.service.NoticeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// Notice Create 과정에 실제 USER 권한이 필요하기 때문에 @WithMockUser 로도 해결할 수 없고
// @WebMvcTest 진행할 수 없어 TEST 수행 전 실제 USER 정보를 넣기 위해 @SpringBootTest 로 진행
@Transactional
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class NoticeApiControllerTest {

    private static Long NOTICE_ID;
    private final String TITLE = "panda";
    private final String CONTENT = "bear";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    NoticeService noticeService;
    @Autowired
    NoticeRepository noticeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        User user = User.builder()
            .username("PANDA")
            .password("123456")
            .build();

        userRepository.save(user);

        Notice notice = Notice.builder()
            .title(TITLE)
            .content(CONTENT)
            .startDateTime(LocalDateTime.now())
            .endDateTime(LocalDateTime.of(2022, 12, 31, 0, 0, 0))
            .build();
        NOTICE_ID = noticeRepository.save(notice).getId();
    }

    @Test
    @DisplayName("Notice List 반환 - 200")
    void getNoticeList() throws Exception {
        mockMvc.perform(
                get("/api/notices").accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded").exists())
            .andExpect(jsonPath("$._links").exists())
            .andExpect(jsonPath("$.page").exists());
    }

    @Test
    @DisplayName("게시글 작성 - 201 multipart/form-data 형식")
    @WithMockUser(username = "PANDA", roles = "USER")
    void createNotice() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String startDateTime = LocalDateTime.now().format(formatter);
        String endDateTime = LocalDateTime.of(2034, 12, 31, 4, 0, 0).format(formatter);

        NoticeImageRequest noticeImageRequest = NoticeImageRequest.builder()
            .title(TITLE)
            .content(CONTENT)
            .startDateTime(startDateTime)
            .endDateTime(endDateTime)
            .files(Collections.emptyList())
            .build();

        System.out.println("noticeImageRequest = " + noticeImageRequest);

        mockMvc.perform(
                post("/api/notices")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(noticeImageRequest)))
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 단건 조회")
    void getNotice() throws Exception {
        mockMvc.perform(
                get("/api/notices/" + NOTICE_ID).accept(MediaType.ALL))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("content").exists())
            .andExpect(jsonPath("createdDateTime").exists())
            .andExpect(jsonPath("username").exists())
            .andExpect(jsonPath("files").exists())
            .andExpect(jsonPath("_links").exists());
    }

    @Test
    @DisplayName("게시글 수정")
    void editNotice() throws Exception {
        NoticeRequest noticeRequest = NoticeRequest.builder()
            .title("NEW-TITLE")
            .content("NEW-CONTENT")
            .build();

        mockMvc.perform(
                patch("/api/notices/" + NOTICE_ID).accept(MediaType.ALL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(noticeRequest)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void deleteNotice() throws Exception {
        mockMvc.perform(
                delete("/api/notices/" + NOTICE_ID).accept(MediaType.ALL))
            .andDo(print())
            .andExpect(status().isNoContent());

    }
}