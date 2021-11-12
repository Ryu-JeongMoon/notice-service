package com.example.noticeservice.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.noticeservice.domain.user.entity.dto.request.UserRequest;
import com.example.noticeservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class UserApiControllerTest {

    private final String USERNAME = "PANDA";
    private final String PASSWORD = "BEAR";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserService userService;

    @BeforeEach
    void init() {
        UserRequest userRequest = UserRequest.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .build();
        userService.register(userRequest);
    }

    @Test
    @DisplayName("회원가입 성공 - 201")
    @WithAnonymousUser
    void signup() throws Exception {
        UserRequest userRequest = UserRequest.builder()
            .username(USERNAME + 1)
            .password(PASSWORD + 1)
            .build();

        mockMvc.perform(
                post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(userRequest)))
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원가입 실패 - 400 Valid 검증 실패")
    @WithAnonymousUser
    void signupFail() throws Exception {
        UserRequest userRequest = UserRequest.builder()
            .username(USERNAME)
            .password(PASSWORD + PASSWORD + PASSWORD)
            .build();

        mockMvc.perform(
                post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(userRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 성공 - 200")
    @WithAnonymousUser
    void login() throws Exception {
        UserRequest userRequest = UserRequest.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .build();

        mockMvc.perform(
                post("/api/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 실패 - 403 익명 권한 필요")
    @WithMockUser(username = "PANDA_BEAR", roles = "ADMIN")
    void loginAuthorityFail() throws Exception {
        UserRequest userRequest = UserRequest.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .build();

        mockMvc.perform(
                post("/api/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest)))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인 실패 - 401 잘못된 비밀번호")
    @WithAnonymousUser
    void loginCredentialFail() throws Exception {
        UserRequest userRequest = UserRequest.builder()
            .username(USERNAME)
            .password(PASSWORD + PASSWORD)
            .build();

        mockMvc.perform(
                post("/api/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }
}