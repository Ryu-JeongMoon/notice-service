package com.example.noticeservice.domain.user.mapper;

import com.example.noticeservice.domain.user.entity.User;
import com.example.noticeservice.domain.user.entity.dto.response.UserResponse;
import com.example.noticeservice.util.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserResponseMapper extends GenericMapper<UserResponse, User> {

}
