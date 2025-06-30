package com.whatsapp.chatbot.controller;


import com.whatsapp.chatbot.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Autowired
    private FirebaseService firebaseService;

    @GetMapping
    public ResponseEntity<String> verifyWebhook(@RequestParam("hub.mode") String mode,
                                                @RequestParam("hub.challenge") String challenge,
                                                @RequestParam("hub.verify_token") String token) {
        String VERIFY_TOKEN = "whatsapp-bot-token";
        if("subscribe".equals(mode) && VERIFY_TOKEN.equals(token)) {
            return ResponseEntity.ok(challenge);
        } else {
            return ResponseEntity.status(403).body("Verification failed");
        }
    }

    @PostMapping
    public ResponseEntity<String> recieveWebhook(@RequestBody Map<String, Object> body) {
        System.out.println(" Webhook POST endpoint HIT!");
        System.out.println(" Raw Payload: " + body);

        try {
            Map entry = ((List<Map>) body.get("entry")).get(0);
            Map change = ((List<Map>) entry.get("changes")).get(0);
            Map value = (Map) change.get("value");

            List<Map> messages = (List<Map>) value.get("messages");
            List<Map> contacts = (List<Map>) value.get("contacts");

            if (messages != null && !messages.isEmpty()) {
                Map message = messages.get(0);
                Map text = (Map) message.get("text");
                String userMessage = (String) text.get("body");
                String userNumber = (String) message.get("from");

                String userName = (String) ((Map) contacts.get(0).get("profile")).get("name");

                System.out.println(" From: " + userName + " (" + userNumber + ")");
                System.out.println(" Message: " + userMessage);


                String replyText;
                switch (userMessage.trim().toLowerCase()) {
                    case "hi":
                    case "hello":
                        replyText = "Hi there! How can I help you today?";
                        break;
                    case "help":
                        replyText = "You can ask me about our services, working hours, or say 'bye' to end.";
                        break;
                    case "bye":
                        replyText = "Bye! Have a great day!";
                        break;
                    default:
                        replyText = "Sorry, I didn’t understand that. Try saying 'help'.";
                }


                firebaseService.saveChat(userName, userNumber, userMessage, replyText);


                String TOKEN = System.getenv("WHATSAPP_TOKEN");

                String PHONE_NUMBER_ID = "621107784430349";

                String url = "https://graph.facebook.com/v18.0/" + PHONE_NUMBER_ID + "/messages";

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(TOKEN);

                Map<String, Object> msg = Map.of(
                        "messaging_product", "whatsapp",
                        "to", userNumber,
                        "type", "text",
                        "text", Map.of("body", replyText)
                );

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(msg, headers);
                RestTemplate restTemplate = new RestTemplate();

                try {
                    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                    System.out.println(" Auto-reply sent: " + response.getBody());
                } catch (Exception e) {
                    System.err.println(" Failed to send auto-reply: " + e.getMessage());
                }
            }


        } catch (Exception e) {
            System.err.println(" Failed to parse incoming message: " + e.getMessage());
        }

        return ResponseEntity.ok("EVENT_RECEIVED");
    }

//    private void sendAutoReply(String to, String userMessage) {
//        String TOKEN = "EAAPFgqRIILIBO8qVyeTGhH4KUi9ww3dZCAMdaQdVhzurgVHEmhxVwATyzy4lEsV1vo7sC4CFIkWrAfh8HaI67nx56pdnM9vCDjGZBrp6N0OspgR45x96tNMwrxwjTSAJQC5PwE5iv1VjKlZBqMZAzNHeceSZBKY9ZAAaXzBuH9oUNvYSQJ7R8ZBKxiATsM2xp0CiPOGxZCQ8iLa6xKvtaNj48goTGZCtV2imZBzs4G20QglgZDZD";
//        String PHONE_NUMBER_ID = "621107784430349";
//
//        String replyText;
//
//        switch (userMessage.trim().toLowerCase()) {
//            case "hi":
//            case "hello":
//                replyText = " Hi there! How can I help you today?";
//                break;
//            case "help":
//                replyText = " You can ask me about our services, working hours, or say 'bye' to end.";
//                break;
//            case "bye":
//                replyText = " Bye! Have a great day!";
//                break;
//            default:
//                replyText = " Sorry, I didn’t understand that. Try saying 'help'.";
//        }
//
//        String url = "https://graph.facebook.com/v18.0/" + PHONE_NUMBER_ID + "/messages";
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(TOKEN);
//
//        Map<String, Object> message = Map.of(
//                "messaging_product", "whatsapp",
//                "to", to,
//                "type", "text",
//                "text", Map.of("body", replyText)
//        );
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);
//
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
//            System.out.println(" Auto-reply sent: " + response.getBody());
//        } catch (Exception e) {
//            System.err.println(" Failed to send auto-reply: " + e.getMessage());
//        }
//    }


}
