package com.example.noticeservice.controller.view;

import com.example.noticeservice.util.Messages;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/notices")
public class NoticeViewController {

    private WebClient webClient = WebClient.create(Messages.DEFAULT_URL);

    @GetMapping
    public String getListForm(@PageableDefault Pageable pageable, Model model) {
        Flux<List> noticeList = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/api/notices").queryParam("pageable", pageable).build())
            .retrieve()
            .bodyToFlux(List.class);

        model.addAttribute("noticeList", noticeList);
        return "notice/get-list";
    }

    @GetMapping("/{noticeId}")
    public String getForm(@PathVariable Long noticeId, Model model) {
        Mono<String> notice = webClient.get()
            .uri("/api/notices/" + noticeId)
            .retrieve()
            .bodyToMono(String.class);

        model.addAttribute("notice", notice);
        return "notice/get";
    }

    @GetMapping("/create")
    public String createForm() {
        return "notice/create";
    }

    @GetMapping("/edit/{noticeId}")
    public String editForm(@PathVariable Long noticeId, Model model) {
        Mono<String> notice = webClient.get()
            .uri("/api/notices/" + noticeId)
            .retrieve()
            .bodyToMono(String.class);

        model.addAttribute("notice", notice);
        return "notice/edit";
    }

    @GetMapping("/delete/{noticeId}")
    public String deleteForm(@PathVariable Long noticeId, Model model) {
        Mono<String> notice = webClient.get()
            .uri("/api/notices/" + noticeId)
            .retrieve()
            .bodyToMono(String.class);

        model.addAttribute("notice", notice);
        return "notice/delete";
    }
}
