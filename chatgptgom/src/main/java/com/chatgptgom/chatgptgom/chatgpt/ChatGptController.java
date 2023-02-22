package com.chatgptgom.chatgptgom.chatgpt;

import com.chatgptgom.chatgptgom.common.FormInputDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class ChatGptController {

    private static final String MAIN_PAGE = "index";

    @Autowired private ObjectMapper jsonMapper;
    @Autowired private OpenAiApiClient client;

    @GetMapping(path = "/")
    public String index() {
        return MAIN_PAGE;
    }

    private String chatWithGpt3(String message) throws Exception {
        log.info("chatGPT3 MSG : " + message);
        var completion = CompletionRequest.defaultWith(message);
        var postBodyJson = jsonMapper.writeValueAsString(completion);
        var responseBody = client.postToOpenAiApi(postBodyJson, OpenAiApiClient.OpenAiService.GPT_3);
        var completionResponse = jsonMapper.readValue(responseBody, CompletionResponse.class);
        return completionResponse.firstAnswer().orElseThrow();
    }

    @PostMapping(path = "/")
    public String chat(Model model, @ModelAttribute FormInputDTO dto) {


        try {
            log.info("ChatGPT Post request " +  dto.prompt());
            model.addAttribute("request", dto.prompt());
            log.info("ChatGPT Post Response " + chatWithGpt3(dto.prompt()));
            model.addAttribute("response", chatWithGpt3(dto.prompt()));
        } catch (Exception e) {
            model.addAttribute("response", "Error in communication with OpenAI ChatGPT API.");
        }
        return MAIN_PAGE;
    }




}
