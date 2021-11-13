package com.example.noticeservice.service;

import com.example.noticeservice.domain.image.entity.Image;
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
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final ImageProcessor imageProcessor;
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeRequestMapper requestMapper;
    private final NoticeResponseMapper responseMapper;

    // 대용량 트래픽을 처리하기 위한 Redis 도입을 고려했으나 페이징 형태로 결과를 보여주기 때문에
    // ArrayList 에서의 삭제와 같이 중간에 하나만 삭제하더라도 그 뒤에 있는 것들이 전부 shift 되어야 하기 때문에
    // 페이징과는 궁합이 좋지 않아보여 도입하지 않기로 함
    @Transactional(readOnly = true)
    public Page<NoticeResponse> getNotices(Pageable pageable) {
        return noticeRepository.findByStatus(pageable, Status.ACTIVE);
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
            .orElseThrow(() -> new EntityNotFoundException(Messages.NOTICE_NOT_FOUND));
    }

    // create 과정에 작성자 입력 없이 추가할 수 있도록 SecurityContextHolder 이용
    @Transactional
    public void create(NoticeRequest noticeRequest, List<MultipartFile> files) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException(Messages.USER_NOT_FOUND));

        noticeRequest = convertBlankToNull(noticeRequest);
        Notice notice = requestMapper.toEntity(noticeRequest);
        notice.setUser(user);

        List<Image> images = imageProcessor.parse(files);
        notice.addImages(images);

        noticeRepository.save(notice);
    }

    // 활성 상태의 게시글만 수정 가능하도록 findById -> findByIdAndStatus 변경
    // images 갖고 있지 않는 notice 의 경우 join fetch 시 No value present 에러 발생, edit 과정에서 조건 분기에 따라 해결하거나 첨부파일을 필수로 받아야 할듯
    // -> 연관관계의 주인이 아닌 곳, 즉 외래키를 가지고 있지 않은 곳에서 호출하면 항상 EAGER 로 작동, 참조하고 있는 값이 null 인지 아닌지 확인이 불가하기 때문
    // MapStruct 의 설정에 따라 null 인 경우에 매핑하지 않도록 하기 위해 "" 로 들어오는 값 -> null 로 치환
    @Transactional
    public void edit(Long id, NoticeRequest noticeRequest, List<MultipartFile> files) throws Exception {
        Notice notice = noticeRepository.findByIdAndStatus(id, Status.ACTIVE)
            .orElseThrow(() -> new EntityNotFoundException(Messages.NOTICE_NOT_FOUND));

        noticeRequest = convertBlankToNull(noticeRequest);

        List<Image> images = imageProcessor.parse(files);
        notice.addImages(images);

        requestMapper.updateFromDto(noticeRequest, notice);
    }

    // title, content 빈 문자열로 들어오는 경우, 매핑되어버려 DB에 빈 값으로 저장됨
    // 빈 문자열로 들어오는 경우 Mapstruct 에 의해 매핑되지 않도록 하려면 null 로 변환해줘야 함
    private NoticeRequest convertBlankToNull(NoticeRequest noticeRequest) {
        String title = noticeRequest.getTitle();
        title = StringUtils.hasText(title) ? title : null;
        noticeRequest.setTitle(title);

        String content = noticeRequest.getContent();
        content = StringUtils.hasText(content) ? content : null;
        noticeRequest.setContent(content);

        return noticeRequest;
    }

    @Transactional
    public void delete(Long id) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(Messages.NOTICE_NOT_FOUND));

        notice.changeStatus();
    }
}
