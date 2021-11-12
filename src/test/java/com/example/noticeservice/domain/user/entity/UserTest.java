package com.example.noticeservice.domain.user.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserTest {

    private User user = User.builder()
        .username("PANDA")
        .password("BEAR")
        .build();

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("password encoder 를 통해 인코딩한 값으로 들어간다")
    void setPasswordAs() {
        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        user.setPasswordAs(encodedPassword);
        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());

        assertThat(matches).isTrue();
    }

    @Test
    @DisplayName("권한 변경")
    void changeRole() {
        user.changeRole(Role.ROLE_ADMIN);
        assertThat(user.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    @DisplayName("권한 기본 값 ROLE_USER")
    void getRole() {
        assertThat(user.getRole()).isEqualTo(Role.ROLE_USER);
    }
}