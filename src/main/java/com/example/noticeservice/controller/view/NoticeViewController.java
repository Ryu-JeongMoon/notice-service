package com.example.noticeservice.controller.view;

import com.example.noticeservice.domain.image.entity.dto.response.ImageResponse;
import com.example.noticeservice.domain.notice.entity.dto.request.NoticeImageRequest;
import com.example.noticeservice.domain.notice.entity.dto.response.NoticeResponse;
import com.example.noticeservice.domain.user.entity.dto.response.UserResponse;
import com.example.noticeservice.util.Messages;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeViewController {

    private final ObjectMapper objectMapper;
    private WebClient webClient = WebClient.create(Messages.DEFAULT_URL);

    // service 계층을 사용하지 않고 front-back 처럼 동작하게 만들기 위해 webClient 이용해 Controller 호출, REST API 사용
    // JSON Object 뷰로 내리기 위한 하드코딩
    @GetMapping
    public String getListForm(@PageableDefault Pageable pageable, Model model) {
        LinkedHashMap result = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/api/notices").queryParam("pageable", pageable).build())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(LinkedHashMap.class)
            .block();

        LinkedHashMap _embeddedMap = objectMapper.convertValue(result.get("_embedded"), LinkedHashMap.class);
        ArrayList<LinkedHashMap> arrayList = objectMapper.convertValue(_embeddedMap.get("noticeResponseList"), ArrayList.class);

        List<NoticeResponse> noticeList = arrayList.stream()
            .map(o -> NoticeResponse.builder()
                .id(Long.valueOf((Integer) o.get("id")))
                .title((String) o.get("title"))
                .content((String) o.get("content"))
                .createdDateTime(LocalDateTime.parse((String) o.get("createdDateTime")))
                .hit((int) o.get("hit"))
                .userResponse(
                    UserResponse.from(StringUtils.hasText((String) o.get("username")) ? (String) o.get("username") : "panda"))
                .build())
            .collect(Collectors.toList());

        model.addAttribute("noticeList", noticeList);
        return "notice/get-list";
    }

    @GetMapping("/{noticeId}")
    public String getForm(@PathVariable Long noticeId, Model model) {
        JSONObject jsonObject = webClient.get()
            .uri("/api/notices/" + noticeId)
            .retrieve()
            .bodyToMono(JSONObject.class)
            .block();

        NoticeResponse noticeResponse = NoticeResponse.builder()
            .id(Long.valueOf(jsonObject.getAsString("id")))
            .title(jsonObject.getAsString("title"))
            .content(jsonObject.getAsString("content"))
            .hit(Integer.parseInt(jsonObject.getAsString("hit")))
            .userResponse(UserResponse.from(jsonObject.getAsString("username")))
            .build();

        JSONObject jsonImagesResult = webClient.get()
            .uri("/api/images/" + noticeId)
            .retrieve()
            .bodyToMono(JSONObject.class)
            .block();

        LinkedHashMap _embeddedMap = objectMapper.convertValue(jsonImagesResult.get("_embedded"), LinkedHashMap.class);
        ArrayList<LinkedHashMap> arrayList = objectMapper.convertValue(_embeddedMap.get("imageResponseList"), ArrayList.class);
        List<ImageResponse> imageResponses = arrayList.stream()
            .map(o -> ImageResponse.builder()
                .origFileName((String) o.get("origFileName"))
                .fileName((String) o.get("fileName"))
                .filePath((String) o.get("filePath"))
                .fileSize((long) (int) o.get("fileSize"))
                .build())
            .collect(Collectors.toList());

        for (ImageResponse imageResponse : imageResponses) {
            System.out.println("imageResponse.getFilePath() = " + imageResponse.getFilePath());
        }

        model.addAttribute("noticeResponse", noticeResponse);
        model.addAttribute("imageResponses", imageResponses);
        return "notice/get";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("noticeImageRequest", new NoticeImageRequest());
        return "notice/create";
    }

    @GetMapping("/edit/{noticeId}")
    public String editForm(@PathVariable Long noticeId, Model model) {
        JSONObject jsonObject = webClient.get()
            .uri("/api/notices/" + noticeId)
            .retrieve()
            .bodyToMono(JSONObject.class)
            .block();

        NoticeResponse noticeResponse = NoticeResponse.builder()
            .id(Long.valueOf(jsonObject.getAsString("id")))
            .title(jsonObject.getAsString("title"))
            .content(jsonObject.getAsString("content"))
            .createdDateTime(LocalDateTime.parse(jsonObject.getAsString("createdDateTime")))
            .hit(Integer.parseInt(jsonObject.getAsString("hit")))
            .userResponse(UserResponse.from(jsonObject.getAsString("username")))
            .build();

        model.addAttribute("notice", noticeResponse);
        model.addAttribute("noticeImageRequest", new NoticeImageRequest());
        return "notice/edit";
    }
}
