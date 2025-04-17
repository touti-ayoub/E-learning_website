import { Component, OnInit } from '@angular/core';
import { ChatbotService } from '../../services/chatbot.service';
import { ChatMessage, ChatSession } from '../../models/chat.model';

@Component({
  selector: 'app-chatbot',
  templateUrl: './chatbot.component.html',
  styleUrls: ['./chatbot.component.css']
})
export class ChatbotComponent implements OnInit {
  currentSession: ChatSession | null = null;
  message: string = '';
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private chatbotService: ChatbotService
  ) { }

  ngOnInit(): void {
    console.log('ChatbotComponent initialized');
    // Create a new session immediately for anonymous use
    this.createNewSession();
  }

  createNewSession(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.chatbotService.createSession().subscribe({
      next: (session) => {
        console.log('Created new session:', session);
        this.currentSession = session;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error creating new session:', err);
        this.errorMessage = 'Failed to create a new chat session. Please try again later.';
        this.isLoading = false;
      }
    });
  }

  sendMessage(): void {
    if (!this.message.trim() || !this.currentSession?.id) {
      console.warn('Cannot send message: Missing data', {
        messageEmpty: !this.message.trim(),
        noSessionId: !this.currentSession?.id
      });
      return;
    }
    
    console.log('Sending message:', this.message);
    const userMessage: ChatMessage = {
      role: 'user',
      content: this.message,
      timestamp: new Date(),
      chatSessionId: this.currentSession.id
    };

    // Add user message to UI immediately
    if (!this.currentSession.messages) {
      this.currentSession.messages = [];
    }
    this.currentSession.messages.push(userMessage);
    
    // Clear input field
    const messageSent = this.message;
    this.message = '';
    
    // Show loading state
    this.isLoading = true;
    this.errorMessage = '';
    
    // Send to backend
    this.chatbotService.sendMessage(this.currentSession.id, messageSent).subscribe({
      next: (response) => {
        console.log('Received response:', response);
        // Update the current session with the bot's response
        this.currentSession?.messages.push(response);
        this.isLoading = false;
        
        // Scroll to bottom after message is added
        setTimeout(() => this.scrollToBottom(), 100);
      },
      error: (err) => {
        console.error('Error sending message:', err);
        this.isLoading = false;
        this.errorMessage = 'Failed to send message. Please try again.';
      }
    });
  }

  private scrollToBottom(): void {
    const chatContainer = document.querySelector('.chat-messages');
    if (chatContainer) {
      chatContainer.scrollTop = chatContainer.scrollHeight;
    }
  }
} 