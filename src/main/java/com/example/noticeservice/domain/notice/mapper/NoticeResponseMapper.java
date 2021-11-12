package com.example.noticeservice.domain.notice.mapper;

import com.example.noticeservice.domain.notice.entity.Notice;
import com.example.noticeservice.domain.notice.entity.dto.response.NoticeResponse;
import com.example.noticeservice.util.mapper.GenericMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoticeResponseMapper extends GenericMapper<NoticeResponse, Notice> {


    @Override
    @Mappings({@Mapping(source = "user", target = "userResponse")})
    NoticeResponse toDto(Notice notice);

    @Override
    @Mappings({@Mapping(source = "userResponse", target = "user")})
    Notice toEntity(NoticeResponse noticeResponse);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({@Mapping(source = "userResponse", target = "user")})
    void updateFromDto(NoticeResponse dto, @MappingTarget Notice entity);
}
