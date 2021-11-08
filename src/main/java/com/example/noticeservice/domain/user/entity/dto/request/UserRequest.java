package com.example.noticeservice.domain.user.entity.dto.request;

import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    // 문자 + 숫자 + 언더바(_) 만 허용
    @Pattern(regexp = "^[\\w]{4,8}$")
    private String username;

    // 특수 문자도 포함하기 위해 공백 제외 모든 문자 허용
    @Pattern(regexp = "^[\\S]{4,8}$")
    private String password;
}
