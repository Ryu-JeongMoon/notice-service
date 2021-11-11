package com.example.noticeservice.service;

import static org.assertj.core.api.Assertions.*;

import com.example.noticeservice.domain.user.entity.dto.request.UserRequest;
import com.example.noticeservice.domain.user.entity.dto.response.UserResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class UserServiceTest {

    private final String USERNAME = "PANDA";
    private final String PASSWORD = "BEAR";

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
    @DisplayName("회원 가입 성공")
    void register() {
        UserRequest userRequest = UserRequest.builder()
            .username(USERNAME + 1)
            .password(PASSWORD + 1)
            .build();
        userService.register(userRequest);

        UserResponse userResponse = userService.findByUsername(USERNAME + 1);

        assertThat(userResponse.getUsername()).isEqualTo(USERNAME + 1);
    }

    @Test
    @DisplayName("로그인 성공")
    void login() {
        UserRequest userRequest = UserRequest.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .build();
        userService.login(userRequest);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        assertThat(username).isEqualTo(USERNAME);
    }
}