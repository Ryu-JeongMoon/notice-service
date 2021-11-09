package com.example.noticeservice.service;

import com.example.noticeservice.domain.file.entity.Image;
import com.example.noticeservice.domain.file.repository.ImageRepository;
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
import javax.servlet.http.HttpSession;
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

    // TODO, SecurityContextHolder 에서 가져오는 방법으로 변경 가능해보임
    @Transactional
    public void create(NoticeRequest noticeRequest, List<MultipartFile> files, HttpSession httpSession) throws Exception {
        String username = (String) httpSession.getAttribute("username");
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException(Messages.USER_NOT_FOUND));

        String username2 = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("username2 = " + username2);

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
