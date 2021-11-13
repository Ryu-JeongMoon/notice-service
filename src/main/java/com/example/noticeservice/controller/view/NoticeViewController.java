package com.example.noticeservice.controller.view;

import com.example.noticeservice.domain.image.entity.dto.response.ImageResponse;
import com.example.noticeservice.domain.notice.entity.dto.request.NoticeImageRequest;
import com.example.noticeservice.domain.notice.entity.dto.response.NoticeResponse;
import com.example.noticeservice.domain.user.entity.dto.response.UserResponse;
import com.example.noticeservice.util.CustomPage;
import com.example.noticeservice.util.Messages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeViewController {

    private final ObjectMapper objectMapper;
    private final WebClient webClient = WebClient.create(Messages.DEFAULT_URL);

    // service 계층을 사용하지 않고 front-back 처럼 동작하게 만들기 위해 webClient 이용해 Controller 호출, REST API 사용
    // JSON Object 뷰로 내리기 위한 하드코딩
    @GetMapping
    public String getListForm(@PageableDefault Pageable pageable, Model model) {
        LinkedHashMap result = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/api/notices")
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize()).build())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(LinkedHashMap.class)
            .block();

        LinkedHashMap _embeddedMap = objectMapper.convertValue(result.get("_embedded"), LinkedHashMap.class);
        ArrayList<LinkedHashMap> arrayList = objectMapper.convertValue(_embeddedMap.get("noticeResponseList"), ArrayList.class);

        List<NoticeResponse> noticeResponses = arrayList.stream()
            .map(o -> NoticeResponse.builder()
                .id(Long.valueOf((Integer) o.get("id")))
                .title((String) o.get("title"))
                .content((String) o.get("content"))
                .createdDateTime(LocalDateTime.parse((String) o.get("createdDateTime")))
                .hit((int) o.get("hit"))
                .userResponse(
                    UserResponse.from(
                        (String) objectMapper.convertValue(o.get("userResponse"), LinkedHashMap.class).get("username")))
                .build())
            .collect(Collectors.toList());

        LinkedHashMap<String, Integer> pageMap = objectMapper.convertValue(result.get("page"), new TypeReference<>() {
        });

        CustomPage page = CustomPage.builder()
            .size(pageMap.get("size"))
            .number(pageMap.get("number"))
            .totalPages(pageMap.get("totalPages"))
            .totalElements(pageMap.get("totalElements"))
            .build();

        model.addAttribute("page", page);
        model.addAttribute("noticeResponses", noticeResponses);
        return "notice/get-notice-list";
    }

    @GetMapping("/{noticeId}")
    public String getForm(@PathVariable Long noticeId, Model model) {
        NoticeResponse noticeResponse = requestNotice(noticeId);
        List<ImageResponse> imageResponses = requestImages(noticeId);

        model.addAttribute("noticeResponse", noticeResponse);
        model.addAttribute("imageResponses", imageResponses);
        return "notice/get-notice";
    }

    private NoticeResponse requestNotice(Long noticeId) {
        JSONObject jsonObject = webClient.get()
            .uri("/api/notices/" + noticeId)
            .retrieve()
            .bodyToMono(JSONObject.class)
            .block();

        return NoticeResponse.builder()
            .id(Long.valueOf(jsonObject.getAsString("id")))
            .title(jsonObject.getAsString("title"))
            .content(jsonObject.getAsString("content"))
            .endDateTime(LocalDateTime.parse(jsonObject.getAsString("endDateTime")))
            .startDateTime(LocalDateTime.parse(jsonObject.getAsString("startDateTime")))
            .createdDateTime(LocalDateTime.parse(jsonObject.getAsString("createdDateTime")))
            .hit(Integer.parseInt(jsonObject.getAsString("hit")))
            .userResponse(
                UserResponse.from(
                    (String) objectMapper.convertValue(jsonObject.get("userResponse"), LinkedHashMap.class).get("username")))
            .build();
    }

    private List<ImageResponse> requestImages(Long noticeId) {
        JSONObject jsonImagesResult = webClient.get()
            .uri("/api/images/" + noticeId)
            .retrieve()
            .bodyToMono(JSONObject.class)
            .block();

        LinkedHashMap _embeddedMap = objectMapper.convertValue(jsonImagesResult.get("_embedded"), LinkedHashMap.class);

        if (_embeddedMap == null) {
            return Collections.emptyList();
        }

        ArrayList<LinkedHashMap> arrayList = objectMapper.convertValue(_embeddedMap.get("imageResponseList"), ArrayList.class);

        return arrayList.stream()
            .map(o -> ImageResponse.builder()
                .originalFileName((String) o.get("origFileName"))
                .fileName((String) o.get("fileName"))
                .filePath((String) o.get("filePath"))
                .fileSize((long) (int) o.get("fileSize"))
                .id((long) (int) o.get("id"))
                .build())
            .collect(Collectors.toList());
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("noticeImageRequest", new NoticeImageRequest());
        return "notice/create-notice";
    }

    // 게시글 정보를 나타낼 noticeResponse, 이미지 정보를 나타낼 imageResponses, 업데이트될 정보를 담을 NoticeImageRequest 뷰로 내림
    @GetMapping("/edit/{noticeId}")
    public String editForm(@PathVariable Long noticeId, Model model) {
        NoticeResponse noticeResponse = requestNotice(noticeId);
        List<ImageResponse> imageResponses = requestImages(noticeId);

        model.addAttribute("noticeResponse", noticeResponse);
        model.addAttribute("imageResponses", imageResponses);
        model.addAttribute("noticeImageRequest", new NoticeImageRequest());
        return "notice/edit-notice";
    }

    @PostMapping(value = "/edit/{noticeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String edit(@PathVariable Long noticeId, @ModelAttribute NoticeImageRequest noticeImageRequest)
        throws JsonProcessingException {

        LinkedMultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("title", objectMapper.writeValueAsString(noticeImageRequest.getTitle()));
        multiValueMap.add("content", objectMapper.writeValueAsString(noticeImageRequest.getContent()));
        multiValueMap.add("files", objectMapper.writeValueAsString(noticeImageRequest.getFiles()));

        return webClient.post()
            .uri("/api/notices/" + noticeId)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromFormData(multiValueMap))
            .exchangeToMono(Mono::just)
            .map(ClientResponse::statusCode)
            .filter(HttpStatus::is2xxSuccessful)
            .map(httpStatus -> "redirect:/notices/" + noticeId)
            .block();
    }
}

//        return webClient.patch()
//            .uri("/api/notices/" + noticeId)
//            .contentType(MediaType.APPLICATION_JSON)
//            .syncBody(noticeImageRequest)
//            .exchangeToMono(Mono::just)
//            .map(ClientResponse::statusCode)
//            .filter(HttpStatus::is2xxSuccessful)
//            .map(httpStatus -> "redirect:/notices/" + noticeId)
//            .block();
