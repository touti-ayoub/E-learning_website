import { Component, OnInit } from '@angular/core';
import { ChatbotService } from '../../services/chatbot.service';
import { ChatMessage, ChatSession } from '../../models/chat.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-chatbot',
  templateUrl: './chatbot.component.html',
  styleUrls: ['./chatbot.component.css']
})
export class ChatbotComponent implements OnInit {
  userId: number | null = null;
  sessions: ChatSession[] = [];
  currentSession: ChatSession | null = null;
  message: string = '';
  isLoading: boolean = false;
  isSidebarOpen: boolean = false;

  constructor(
    private chatbotService: ChatbotService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    // Get user ID from auth service
    this.authService.getCurrentUser().subscribe(user => {
      if (user && user.id) {
        this.userId = user.id;
        this.loadUserSessions();
      }
    });
  }

  loadUserSessions(): void {
    if (!this.userId) return;
    
    this.isLoading = true;
    this.chatbotService.getUserSessions(this.userId).subscribe({
      next: (sessions) => {
        this.sessions = sessions;
        // If we have sessions, load the most recent one
        if (sessions.length > 0) {
          this.selectSession(sessions[0]);
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading chat sessions', err);
        this.isLoading = false;
      }
    });
  }

  createNewSession(): void {
    if (!this.userId) return;
    
    this.isLoading = true;
    this.chatbotService.createSession(this.userId, 'New Conversation').subscribe({
      next: (session) => {
        this.sessions.unshift(session); // Add to beginning of array
        this.selectSession(session);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error creating new session', err);
        this.isLoading = false;
      }
    });
  }

  selectSession(session: ChatSession): void {
    this.currentSession = session;
    this.isSidebarOpen = false; // Close sidebar on mobile after selection
  }

  sendMessage(): void {
    if (!this.message.trim() || !this.userId || !this.currentSession?.id) return;
    
    const userMessage: ChatMessage = {
      userId: this.userId,
      role: 'user',
      content: this.message,
      timestamp: new Date(),
      chatSessionId: this.currentSession.id
    };

    // Add user message to UI immediately
    this.currentSession.messages.push(userMessage);
    
    // Clear input field
    const messageSent = this.message;
    this.message = '';
    
    // Show loading state
    this.isLoading = true;
    
    // Send to backend
    this.chatbotService.sendMessage(this.userId, this.currentSession.id, messageSent).subscribe({
      next: (response) => {
        // Update the current session with the bot's response
        this.currentSession?.messages.push(response);
        this.isLoading = false;
        
        // Scroll to bottom after message is added
        setTimeout(() => this.scrollToBottom(), 100);
      },
      error: (err) => {
        console.error('Error sending message', err);
        this.isLoading = false;
      }
    });
  }

  toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  private scrollToBottom(): void {
    const chatContainer = document.querySelector('.chat-messages');
    if (chatContainer) {
      chatContainer.scrollTop = chatContainer.scrollHeight;
    }
  }
} 