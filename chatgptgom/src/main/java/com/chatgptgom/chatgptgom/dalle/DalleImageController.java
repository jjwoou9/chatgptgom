package com.chatgptgom.chatgptgom.dalle;

import com.chatgptgom.chatgptgom.chatgpt.OpenAiApiClient;
import com.chatgptgom.chatgptgom.common.FormInputDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class DalleImageController {
	
	public static final String IMAGE_PAGE = "image";
	
	@Autowired private ObjectMapper jsonMapper;
	@Autowired private OpenAiApiClient client;

	private String drawImageWithDallE(String prompt) throws Exception {
		log.info("draw iamge with DallE param " + prompt);

		var generation = GenerationRequest.defaultWith(prompt);

		log.info("draw iamge with DallE generation " + generation);
		var postBodyJson = jsonMapper.writeValueAsString(generation);

		var responseBody = client.postToOpenAiApi(postBodyJson, OpenAiApiClient.OpenAiService.DALL_E);
		log.info("draw iamge with DallE responseBody " + responseBody);

		var completionResponse = jsonMapper.readValue(responseBody, GenerationResponse.class);
		return completionResponse.firstImageUrl().orElseThrow();
	}
	
	@GetMapping(IMAGE_PAGE)
	public String paintImage() {
		return IMAGE_PAGE;
	}
	
	@PostMapping(IMAGE_PAGE)
	public String drawImage(Model model, FormInputDTO dto) throws Exception {
		model.addAttribute("request", dto.prompt());
		model.addAttribute("imageUri", drawImageWithDallE(dto.prompt()));
		return IMAGE_PAGE;
	}

}
