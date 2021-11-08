package com.example.noticeservice.domain.user.entity.dto.response;

import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    @Pattern(regexp = "^[\\w]{4,8}$")
    private String username;
}
