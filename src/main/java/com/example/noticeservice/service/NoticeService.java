package com.example.noticeservice.service;

import com.example.noticeservice.domain.image.entity.Image;
import com.example.noticeservice.domain.image.repository.ImageRepository;
import com.example.noticeservice.domain.notice.entity.Notice;
import com.example.noticeservice.domain.notice.entity.Status;
import com.example.noticeservice.domain.notice.entity.dto.request.NoticeRequest;
import com.example.noticeservice.domain.notice.entity.dto.response.NoticeResponse;
import com.example.noticeservice.domain.notice.mapper.NoticeRequestMapper;
import com.example.noticeservice.domain.notice.mapper.NoticeResponseMapper;
import com.example.noticeservice.domain.notice.repository.NoticeRepository;
import com.example.noticeservice.domain.user.entity.User;
import com.example.noticeservice.domain.user.repository.UserRepository;
import com.example.noticeservice.util.Messages;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeRequestMapper requestMapper;
    private final NoticeResponseMapper responseMapper;
    private final ImageProcessor imageProcessor;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    // 대용량 트래픽을 처리하기 위한 Redis 도입을 고려했으나 페이징 형태로 결과를 보여주기 때문에
    // ArrayList 에서의 삭제와 같이 중간에 하나만 삭제하더라도 그 뒤에 있는 것들이 전부 shift 되어야 하기 때문에
    // 페이징과는 궁합이 좋지 않아보여 도입하지 않기로 함
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

    // files 존재하지 않더라도 게시글 등록 가능하도록 parse() 에서 Empty List 반환
    @Transactional
    public void create(NoticeRequest noticeRequest, List<MultipartFile> files) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("username = " + username);

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException(Messages.USER_NOT_FOUND));

        Notice notice = requestMapper.toEntity(noticeRequest);
        notice.setUser(user);

        List<Image> images = imageProcessor.parse(files);

        images.stream()
            .filter(Objects::nonNull)
            .map(imageRepository::save)
            .forEach(notice::addImage);

        noticeRepository.save(notice);
    }

    @Transactional
    public void edit(Long id, NoticeRequest noticeRequest, List<MultipartFile> files) throws Exception {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(Messages.BOARD_NOT_FOUND));

        List<Image> images = imageProcessor.parse(files);
        images.stream()
            .filter(Objects::nonNull)
            .map(imageRepository::save)
            .forEach(notice::addImage);

        requestMapper.updateFromDto(noticeRequest, notice);
    }

    @Transactional
    public void delete(Long id) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(Messages.BOARD_NOT_FOUND));

        notice.changeStatus();
    }
}
