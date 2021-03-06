package com.example.noticeservice.controller.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.noticeservice.domain.notice.entity.dto.request.NoticeImageRequest;
import com.example.noticeservice.domain.notice.entity.dto.request.NoticeRequest;
import com.example.noticeservice.domain.notice.entity.dto.response.NoticeResponse;
import com.example.noticeservice.service.NoticeService;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeApiController {

    private final NoticeService noticeService;
    private final PagedResourcesAssembler<NoticeResponse> assembler;

    @GetMapping
    public ResponseEntity getNoticeList(Pageable pageable) {
        Page<NoticeResponse> notices = noticeService.getNotices(pageable);
        PagedModel<EntityModel<NoticeResponse>> entityModels = assembler.toModel(notices);
        return ResponseEntity.ok(entityModels);
    }

    // ???????????? ????????? ???????????? ??????????????? files null check ???, null ??? ??? ??? ????????? ????????????
    @PreAuthorize("hasRole('USER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity createNotice(@Valid @ModelAttribute NoticeImageRequest noticeImageRequest,
        BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult);
        }

        NoticeRequest noticeRequest = noticeImageRequest.toNoticeRequest();
        List<MultipartFile> files = noticeImageRequest.getFiles();
        files = files != null ? files : Collections.emptyList();

        noticeService.create(noticeRequest, files);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity getNotice(@PathVariable Long noticeId) {
        NoticeResponse noticeResponse = noticeService.getNotice(noticeId);

        EntityModel<NoticeResponse> model = EntityModel.of(noticeResponse,
            linkTo(methodOn(NoticeApiController.class).getNotice(noticeId)).withRel("edit-notice"));

        return ResponseEntity.ok(model);
    }

    // v1 - multipart/form-data ??? ?????? Post ???????????? ?????? ????????? PatchMapping -> PostMapping ?????? ??????
    // v2 - REST ????????? ????????? ?????? ViewController ??? ?????? webClient ?????? ??? ApiController ?????? ???????????? ??????
    // v3 - ViewController ????????? ?????? CodecException ??????, Post ?????? ?????? ?????? ???????????? ?????? ??????, webclient ????????? ?????? ?????? ?????? ??????
    // v4 - POST, PATCH ?????? ??????, Post ??? async-await ????????? ??????, Patch ??? ViewController ?????? ?????? ??????
    @RequestMapping(value = "/{noticeId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE},
        method = {RequestMethod.POST, RequestMethod.PATCH})
    public ResponseEntity editNotice(@PathVariable Long noticeId, @ModelAttribute NoticeImageRequest noticeImageRequest)
        throws Exception {

        NoticeRequest noticeRequest = noticeImageRequest.toEditNoticeRequest();
        List<MultipartFile> files = noticeImageRequest.getFiles();

        noticeService.edit(noticeId, noticeRequest, files);
        Link link = linkTo(
            methodOn(NoticeApiController.class).getNotice(noticeId)).withSelfRel();

        return ResponseEntity.ok().body(link);
    }

    // soft-delete ??? ?????? Entity ?????? ?????? ??????
    @DeleteMapping("/{noticeId}")
    public ResponseEntity deleteNotice(@PathVariable Long noticeId) {
        noticeService.delete(noticeId);
        return ResponseEntity.noContent().build();
    }
}
