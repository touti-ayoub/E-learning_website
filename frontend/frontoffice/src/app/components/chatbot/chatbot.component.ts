import { Component, OnInit } from '@angular/core';
import { ChatbotService } from '../../services/chatbot.service';
import { ChatMessage, ChatSession } from '../../models/chat.model';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

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
  errorMessage: string = '';

  constructor(
    private chatbotService: ChatbotService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    console.log('ChatbotComponent initialized');
    this.setupUserId();
  }

  setupUserId(): void {
    // Get user ID from auth service
    console.log('Setting up user ID');
    const storedUser = localStorage.getItem('currentUser');
    
    if (storedUser) {
      try {
        const user = JSON.parse(storedUser);
        this.userId = user.id;
        console.log('User ID from localStorage:', this.userId);
        this.loadUserSessions();
      } catch (error) {
        console.error('Error parsing stored user:', error);
        this.useFallbackUser();
      }
    } else {
      // For auth services that store tokens differently
      if (localStorage.getItem('token')) {
        console.log('Token found but no currentUser object');
        this.useFallbackUser();
      } else {
        console.warn('No authentication found, redirecting to login');
        this.errorMessage = 'Please log in to use the chatbot.';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      }
    }
  }

  useFallbackUser(): void {
    console.warn('Using fallback user ID for development');
    this.userId = 1; // Fallback to user ID 1 for development
    this.loadUserSessions();
  }

  loadUserSessions(): void {
    if (!this.userId) {
      console.error('Cannot load sessions: No user ID available');
      this.errorMessage = 'User ID not available. Please try logging out and in again.';
      return;
    }
    
    console.log('Loading sessions for user ID:', this.userId);
    this.isLoading = true;
    this.chatbotService.getUserSessions(this.userId).subscribe({
      next: (sessions) => {
        console.log('Loaded sessions:', sessions);
        this.sessions = sessions;
        // If we have sessions, load the most recent one
        if (sessions.length > 0) {
          this.selectSession(sessions[0]);
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading chat sessions:', err);
        this.isLoading = false;
        this.errorMessage = 'Failed to load chat sessions. Please try again later.';
      }
    });
  }

  createNewSession(): void {
    console.log('Creating new session, userId:', this.userId);
    if (!this.userId) {
      console.error('Cannot create session: No user ID available');
      this.setupUserId(); // Try to set up the user ID again
      
      if (!this.userId) {
        this.errorMessage = 'Unable to create a new chat session. Please log in again.';
        return;
      }
    }
    
    this.isLoading = true;
    this.errorMessage = '';
    
    console.log('Calling createSession with userId:', this.userId);
    this.chatbotService.createSession(this.userId, 'New Conversation').subscribe({
      next: (session) => {
        console.log('Created new session:', session);
        this.sessions.unshift(session); // Add to beginning of array
        this.selectSession(session);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error creating new session:', err);
        this.isLoading = false;
        this.errorMessage = 'Failed to create a new chat session. Please try again later.';
      }
    });
  }

  selectSession(session: ChatSession): void {
    console.log('Selecting session:', session);
    this.currentSession = session;
    this.isSidebarOpen = false; // Close sidebar on mobile after selection
  }

  sendMessage(): void {
    if (!this.message.trim() || !this.userId || !this.currentSession?.id) {
      console.warn('Cannot send message: Missing data', {
        messageEmpty: !this.message.trim(),
        noUserId: !this.userId,
        noSessionId: !this.currentSession?.id
      });
      return;
    }
    
    console.log('Sending message:', this.message);
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
    this.errorMessage = '';
    
    // Send to backend
    this.chatbotService.sendMessage(this.userId, this.currentSession.id, messageSent).subscribe({
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