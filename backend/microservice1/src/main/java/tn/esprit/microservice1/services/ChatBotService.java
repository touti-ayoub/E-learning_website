package tn.esprit.microservice1.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tn.esprit.microservice1.dto.ChatMessageDto;
import tn.esprit.microservice1.dto.ChatSessionDto;
import tn.esprit.microservice1.dto.UserDto;
import tn.esprit.microservice1.dto.openrouter.OpenRouterMessageDto;
import tn.esprit.microservice1.dto.openrouter.OpenRouterRequestDto;
import tn.esprit.microservice1.dto.openrouter.OpenRouterResponseDto;
import tn.esprit.microservice1.entities.ChatMessage;
import tn.esprit.microservice1.entities.ChatSession;
import tn.esprit.microservice1.entities.User;
import tn.esprit.microservice1.repositories.ChatMessageRepository;
import tn.esprit.microservice1.repositories.ChatSessionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatBotService {

    private final WebClient openRouterWebClient;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserClientService userClientService;

    @Autowired
    public ChatBotService(WebClient openRouterWebClient, 
                          ChatSessionRepository chatSessionRepository,
                          ChatMessageRepository chatMessageRepository,
                          UserClientService userClientService) {
        this.openRouterWebClient = openRouterWebClient;
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userClientService = userClientService;
    }

    public ChatSessionDto createSession(Long userId, String title) {
        // Verify user exists
        UserDto userDto = userClientService.getUserById(userId);
        if (userDto == null) {
            throw new RuntimeException("User not found");
        }
        
        User user = new User();
        user.setId(userDto.getId());
        
        ChatSession session = new ChatSession();
        session.setUser(user);
        session.setTitle(title != null ? title : "New Chat");
        
        ChatSession savedSession = chatSessionRepository.save(session);
        return convertToDto(savedSession);
    }

    public List<ChatSessionDto> getUserSessions(Long userId) {
        User user = new User();
        user.setId(userId);
        
        return chatSessionRepository.findByUserOrderByUpdatedAtDesc(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ChatSessionDto getSessionById(Long sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));
        
        return convertToDto(session);
    }

    public ChatMessageDto sendMessage(Long userId, Long sessionId, String message) {
        // Verify user exists
        if (!userClientService.validateUser(userId)) {
            throw new RuntimeException("User not found");
        }
        
        // Get the chat session
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));
        
        // Create a user entity reference (we don't need to fetch the full user object)
        User user = new User();
        user.setId(userId);
        
        // Save the user message
        ChatMessage userMessage = new ChatMessage(user, "user", message, session);
        chatMessageRepository.save(userMessage);
        
        // Get the conversation history
        List<ChatMessage> history = chatMessageRepository.findByChatSessionOrderByTimestampAsc(session);
        
        // Convert to OpenRouter format
        List<OpenRouterMessageDto> openRouterMessages = convertToOpenRouterMessages(history);
        
        // Add system message for context
        openRouterMessages.add(0, new OpenRouterMessageDto("system", 
                "You are a helpful AI assistant for an E-Learning platform. Provide concise, accurate, and helpful responses."));
        
        // Create the request
        OpenRouterRequestDto request = OpenRouterRequestDto.createDefault(openRouterMessages);
        
        // Send to OpenRouter
        OpenRouterResponseDto response = openRouterWebClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenRouterResponseDto.class)
                .block();
        
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new RuntimeException("Failed to get response from AI service");
        }
        
        // Extract assistant's response
        String assistantResponse = response.getChoices().get(0).getMessage().getContent();
        
        // Save the assistant's response
        ChatMessage assistantMessage = new ChatMessage(user, "assistant", assistantResponse, session);
        ChatMessage savedAssistantMessage = chatMessageRepository.save(assistantMessage);
        
        // Update session's lastUpdated time
        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);
        
        return convertToDto(savedAssistantMessage);
    }
    
    // Helper methods for DTO conversion
    private List<OpenRouterMessageDto> convertToOpenRouterMessages(List<ChatMessage> messages) {
        return messages.stream()
                .map(msg -> new OpenRouterMessageDto(msg.getRole(), msg.getContent()))
                .collect(Collectors.toList());
    }
    
    private ChatMessageDto convertToDto(ChatMessage message) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(message.getId());
        dto.setUserId(message.getUser().getId());
        dto.setRole(message.getRole());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setChatSessionId(message.getChatSession().getId());
        return dto;
    }
    
    private ChatSessionDto convertToDto(ChatSession session) {
        ChatSessionDto dto = new ChatSessionDto();
        dto.setId(session.getId());
        dto.setUserId(session.getUser().getId());
        dto.setTitle(session.getTitle());
        dto.setCreatedAt(session.getCreatedAt());
        dto.setUpdatedAt(session.getUpdatedAt());
        
        // Load messages if this session has any
        List<ChatMessage> messages = chatMessageRepository.findByChatSessionOrderByTimestampAsc(session);
        if (messages != null && !messages.isEmpty()) {
            dto.setMessages(messages.stream().map(this::convertToDto).collect(Collectors.toList()));
        } else {
            dto.setMessages(new ArrayList<>());
        }
        
        return dto;
    }
} 