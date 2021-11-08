package com.example.noticeservice.service;

import com.example.noticeservice.domain.user.entity.Role;
import com.example.noticeservice.domain.user.entity.User;
import com.example.noticeservice.domain.user.entity.dto.request.UserRequest;
import com.example.noticeservice.domain.user.mapper.UserRequestMapper;
import com.example.noticeservice.domain.user.mapper.UserResponseMapper;
import com.example.noticeservice.domain.user.repository.UserRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRequestMapper requestMapper;
    private final UserResponseMapper responseMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserRequest userRequest) {
        User user = requestMapper.toEntity(userRequest);

        String plainPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(plainPassword);
        user.changePassword(encodedPassword);
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void login(UserRequest userRequest) {
        User user = userRepository.findByUsername(userRequest.getUsername())
            .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다"));

        String plainPassword = userRequest.getPassword();
        String encodedPassword = user.getPassword();

        if (!passwordEncoder.matches(plainPassword, encodedPassword)) {
            throw new BadCredentialsException("비밀번호가 맞지 않습니다");
        }
    }
}
