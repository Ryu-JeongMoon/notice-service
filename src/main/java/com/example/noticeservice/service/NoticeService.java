package com.example.noticeservice.service;

import com.example.noticeservice.domain.notice.entity.Notice;
import com.example.noticeservice.domain.notice.entity.Status;
import com.example.noticeservice.domain.notice.entity.dto.request.NoticeRequest;
import com.example.noticeservice.domain.notice.entity.dto.response.NoticeResponse;
import com.example.noticeservice.domain.notice.mapper.NoticeRequestMapper;
import com.example.noticeservice.domain.notice.mapper.NoticeResponseMapper;
import com.example.noticeservice.domain.notice.repository.NoticeRepository;
import com.example.noticeservice.util.Messages;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeRequestMapper requestMapper;
    private final NoticeResponseMapper responseMapper;

    // 대용량 트래픽을 처리하기 위한 Redis 도입 예정
    //@Cacheable
    @Transactional(readOnly = true)
    public List<NoticeResponse> getNotices(Pageable pageable) {
        List<Notice> noticeList = noticeRepository.findByPageAndStatus(pageable, Status.ACTIVE);
        List<NoticeResponse> noticeResponseList = responseMapper.toDtoList(noticeList);
        return noticeResponseList;
    }

    // 조회수 증가를 위해 Query + Command 형태
    @Transactional
    public NoticeResponse getNotice(Long id) {
        return noticeRepository.findByIdAndStatus(id, Status.ACTIVE)
            .map(notice -> {
                notice.increaseHit();
                return notice;
            })
            .map(responseMapper::toDto)
            .orElseThrow(() -> new EntityNotFoundException(Messages.BOARD_NOT_FOUND));
    }

    @Transactional
    public void create(NoticeRequest noticeRequest) {
        Notice notice = requestMapper.toEntity(noticeRequest);
        noticeRepository.save(notice);
    }

    @Transactional
    public void edit(Long id, NoticeRequest noticeRequest) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(Messages.BOARD_NOT_FOUND));

        requestMapper.updateFromDto(noticeRequest, notice);
    }

    @Transactional
    public void delete(Long id) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(Messages.BOARD_NOT_FOUND));

        notice.changeStatus();
    }
}
