package tn.esprit.microservice1.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice1.dto.ChatMessageDto;
import tn.esprit.microservice1.dto.ChatSessionDto;
import tn.esprit.microservice1.services.ChatBotService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@Slf4j
public class ChatBotController {

    private final ChatBotService chatBotService;

    @Autowired
    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionDto> createSession(@RequestBody Map<String, Object> request) {
        try {
            Long userId = null;
            // Handle userId if provided
            if (request.containsKey("userId")) {
                userId = Long.valueOf(request.get("userId").toString());
            }
            
            String title = request.containsKey("title") ? request.get("title").toString() : "New Conversation";
            
            ChatSessionDto session = chatBotService.createSession(userId, title);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            log.error("Error creating chat session", e);
            throw e;
        }
    }

    @GetMapping("/sessions/user/{userId}")
    public ResponseEntity<List<ChatSessionDto>> getUserSessions(@PathVariable Long userId) {
        try {
            List<ChatSessionDto> sessions = chatBotService.getUserSessions(userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            log.error("Error getting user sessions", e);
            throw e;
        }
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ChatSessionDto> getSession(@PathVariable Long sessionId) {
        try {
            ChatSessionDto session = chatBotService.getSessionById(sessionId);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            log.error("Error getting chat session", e);
            throw e;
        }
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageDto> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            Long userId = null;
            // Handle userId if provided
            if (request.containsKey("userId")) {
                userId = Long.valueOf(request.get("userId").toString());
            }
            
            Long sessionId = Long.valueOf(request.get("sessionId").toString());
            String message = request.get("message").toString();
            
            ChatMessageDto response = chatBotService.sendMessage(userId, sessionId, message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending message", e);
            throw e;
        }
    }
    
    // Error handler
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleException(RuntimeException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
} 