package com.example.noticeservice.controller.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.noticeservice.domain.notice.entity.dto.request.NoticeImageRequest;
import com.example.noticeservice.domain.notice.entity.dto.request.NoticeRequest;
import com.example.noticeservice.domain.notice.entity.dto.response.NoticeResponse;
import com.example.noticeservice.service.NoticeService;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeApiController {

    private final NoticeService noticeService;
    private final PagedResourcesAssembler<NoticeResponse> assembler;

    @GetMapping
    public ResponseEntity getNoticeList(@PageableDefault Pageable pageable) {
        List<NoticeResponse> noticeResponses = noticeService.getNotices(pageable);
        PageImpl<NoticeResponse> noticeResponsePage = new PageImpl<>(noticeResponses);
        PagedModel<EntityModel<NoticeResponse>> entityModels = assembler.toModel(noticeResponsePage);
        return ResponseEntity.ok(entityModels);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createNotice(@ModelAttribute(value = "noticeImageRequest") NoticeImageRequest noticeImageRequest,
        HttpSession session) throws Exception {
//        if (bindingResult.hasErrors()) {
//            return ResponseEntity.badRequest().body(bindingResult);
//        }
        System.out.println("noticeImageRequest = " + noticeImageRequest);

        NoticeRequest noticeRequest = noticeImageRequest.toNoticeRequest();
        List<MultipartFile> files = noticeImageRequest.getFiles();

        noticeService.create(noticeRequest, files, session);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity getNotice(@PathVariable Long noticeId) {
        NoticeResponse noticeResponse = noticeService.getNotice(noticeId);

        EntityModel<NoticeResponse> model = EntityModel.of(noticeResponse,
            linkTo(methodOn(NoticeApiController.class).getNotice(noticeId)).withRel("edit-notice"));

        return ResponseEntity.ok(model);
    }

    @PatchMapping("/{noticeId}")
    public ResponseEntity editNotice(@PathVariable Long noticeId, @Valid @RequestPart NoticeImageRequest noticeImageRequest,
        BindingResult bindingResult) throws Exception {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult);
        }

        NoticeRequest noticeRequest = noticeImageRequest.toNoticeRequest();
        List<MultipartFile> files = noticeImageRequest.getFiles();

        noticeService.edit(noticeId, noticeRequest, files);
        Link link = linkTo(
            methodOn(NoticeApiController.class).getNotice(noticeId)).withSelfRel();

        return ResponseEntity.ok().body(link);
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity deleteNotice(@PathVariable Long noticeId) {
        noticeService.delete(noticeId);
        return ResponseEntity.noContent().build();
    }
}