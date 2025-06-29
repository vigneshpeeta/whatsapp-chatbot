package com.whatsapp.chatbot.service;


import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FirebaseService {

    public void saveChat(String userName, String userNumber, String userMessage, String botReply) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            Map<String, Object> user = Map.of(
                    "name", userName,
                    "number", userNumber
            );

            Map<String, Object> message = Map.of(
                    "text", userMessage,
                    "timestamp", Instant.now().toString()
            );

            Map<String, Object> bot = Map.of(
                    "reply", botReply
            );

            Map<String, Object> data = Map.of(
                    "user", user,
                    "message", message,
                    "bot", bot
            );

            DocumentReference ref = db.collection("messages").document(UUID.randomUUID().toString());
            ref.set(data);

            System.out.println(" Chat saved to Firebase (clean design)");

        } catch (Exception e) {
            System.err.println(" Failed to save to Firebase: " + e.getMessage());
        }
    }

}
