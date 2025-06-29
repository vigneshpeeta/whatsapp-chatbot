package com.whatsapp.chatbot.controller;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/whatsapp")

public class WhatsAppController {

    private static final String PHONE_NUMBER_ID = "621107784430349";
    private static final String TOKEN = "EAAPFgqRIILIBOyER7ZCKfgqqgZAWBHc35ZBMZA4ZAzQVZCwO7SWUf5fBZCcVds8mXPZBwiUZAUzUw3U5PdNvKkafmUI7LeWWrhmLR2esVYt3zWHA4CJ0p018gGYWqdq49QQd1wfjeQulHzqbKnVZBy0fc5iZAZB8Ds7TRGqRhI1VJG0dI85RCd0ZAWaF5ZAagPfmAoq9ZB3jJAiZBTrOkolfjVZAeaCieK979D13eJI2bZCrkToNaabAZDZD";
    private static final String RECIPIENT = "919959975198";

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody String messageText) {
        String url = "https://graph.facebook.com/v18.0/" + PHONE_NUMBER_ID + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(TOKEN);

        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "to", RECIPIENT,
                "type", "text",
                "text", Map.of("body", messageText)
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

    }
}
