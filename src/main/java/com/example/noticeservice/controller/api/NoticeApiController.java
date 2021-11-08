package com.example.noticeservice.controller.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.noticeservice.domain.notice.entity.dto.request.NoticeRequest;
import com.example.noticeservice.domain.notice.entity.dto.response.NoticeResponse;
import com.example.noticeservice.service.NoticeService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public ResponseEntity createNotice(@Valid @RequestBody NoticeRequest noticeRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult);
        }

        noticeService.create(noticeRequest);
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
    public ResponseEntity editNotice(
        @PathVariable Long noticeId, @Valid @RequestBody NoticeRequest noticeRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult);
        }

        noticeService.edit(noticeId, noticeRequest);
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
