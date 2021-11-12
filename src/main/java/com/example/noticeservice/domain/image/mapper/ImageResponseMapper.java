package com.example.noticeservice.domain.image.mapper;

import com.example.noticeservice.domain.image.entity.Image;
import com.example.noticeservice.domain.image.entity.dto.response.ImageResponse;
import com.example.noticeservice.util.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageResponseMapper extends GenericMapper<ImageResponse, Image> {

}
