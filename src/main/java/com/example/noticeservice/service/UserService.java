package com.example.noticeservice.service;

import com.example.noticeservice.domain.user.entity.User;
import com.example.noticeservice.domain.user.entity.dto.request.UserRequest;
import com.example.noticeservice.domain.user.mapper.UserRequestMapper;
import com.example.noticeservice.domain.user.mapper.UserResponseMapper;
import com.example.noticeservice.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRequestMapper requestMapper;
    private final UserResponseMapper responseMapper;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // DB 에 인코딩된 비밀번호를 저장하기 위해 Service 계층에서 암호화
    @Transactional
    public void register(UserRequest userRequest) {
        User user = requestMapper.toEntity(userRequest);

        String plainPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(plainPassword);
        user.setPasswordAs(encodedPassword);

        userRepository.save(user);
    }

    public void login(UserRequest userRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = userRequest.toAuthenticationToken();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
