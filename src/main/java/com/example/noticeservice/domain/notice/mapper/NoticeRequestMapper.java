package com.example.noticeservice.domain.notice.mapper;

import com.example.noticeservice.domain.notice.entity.Notice;
import com.example.noticeservice.domain.notice.entity.dto.request.NoticeRequest;
import com.example.noticeservice.util.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoticeRequestMapper extends GenericMapper<NoticeRequest, Notice> {

}
