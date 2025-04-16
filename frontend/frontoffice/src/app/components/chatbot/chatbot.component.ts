import { Component, OnInit } from '@angular/core';
import { ChatbotService } from '../../services/chatbot.service';
import { ChatMessage, ChatSession } from '../../models/chat.model';
import { AuthService } from '../../../services/auth/auth.service';
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
  private lastUserId: number | null = null;

  constructor(
    private chatbotService: ChatbotService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    console.log('ChatbotComponent initialized');
    this.setupUserId();
  }

  clearCurrentData(): void {
    this.userId = null;
    this.sessions = [];
    this.currentSession = null;
    this.lastUserId = null;
    this.errorMessage = '';
  }

  setupUserId(): void {
    console.log('Setting up user ID');
    
    // Get user ID directly from localStorage
    const storedUser = localStorage.getItem('currentUser');
    const token = localStorage.getItem('token');
    
    if (storedUser) {
      try {
        const user = JSON.parse(storedUser);
        if (user && user.id) {
          const newUserId = Number(user.id);
          
          // Check if user has changed
          if (this.lastUserId !== null && this.lastUserId !== newUserId) {
            console.log('User changed from', this.lastUserId, 'to', newUserId);
            this.clearCurrentData(); // Clear previous user's data
          }
          
          this.userId = newUserId;
          this.lastUserId = newUserId;
          console.log('User ID from localStorage:', this.userId);
          this.loadUserSessions();
        } else {
          console.error('User object found but no ID property', user);
          this.errorMessage = 'User ID not available. Please try logging out and in again.';
          setTimeout(() => this.router.navigate(['/login']), 2000);
        }
      } catch (error) {
        console.error('Error parsing stored user:', error);
        this.errorMessage = 'Error with user data. Please try logging out and in again.';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      }
    } else {
      console.warn('No authentication found, redirecting to login');
      this.errorMessage = 'Please log in to use the chatbot.';
      setTimeout(() => this.router.navigate(['/login']), 2000);
    }
  }

  loadUserSessions(): void {
    if (!this.userId) {
      console.error('Cannot load sessions: No user ID available');
      this.errorMessage = 'User ID not available. Please try logging out and in again.';
      return;
    }
    
    console.log('Loading sessions for user ID:', this.userId);
    this.isLoading = true;
    
    // Explicitly clear any existing sessions first to ensure no mixing of user data
    this.sessions = [];
    this.currentSession = null;
    
    this.chatbotService.getUserSessions(this.userId).subscribe({
      next: (sessions) => {
        console.log('Loaded sessions:', sessions);
        // Make sure we're only loading sessions for the current user
        if (sessions && Array.isArray(sessions)) {
          this.sessions = sessions.filter(session => {
            const sessionUserId = session.userId || null;
            const matches = sessionUserId === this.userId;
            if (!matches) {
              console.warn(`Filtering out session ${session.id} with userId ${sessionUserId} (current: ${this.userId})`);
            }
            return matches;
          });
          
          console.log(`Filtered down to ${this.sessions.length} sessions for user ${this.userId}`);
          
          // If we have sessions, load the most recent one
          if (this.sessions.length > 0) {
            this.selectSession(this.sessions[0]);
          } else {
            console.log('No sessions found for this user');
          }
        } else {
          console.error('Received invalid sessions data:', sessions);
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
        // Ensure the session has the correct user ID
        if (!session.userId) {
          session.userId = this.userId as number;
          console.log('Added missing userId to session:', session);
        }
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
    // Make sure the session belongs to the current user
    if (session.userId && session.userId !== this.userId) {
      console.error('Attempted to select session belonging to different user', {
        sessionUserId: session.userId,
        currentUserId: this.userId
      });
      this.errorMessage = 'Cannot access this chat session.';
      return;
    }
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
    
    // Verify the current session belongs to the current user
    if (this.currentSession.userId && this.currentSession.userId !== this.userId) {
      console.error('Cannot send message to another user\'s session', {
        sessionUserId: this.currentSession.userId,
        currentUserId: this.userId
      });
      this.errorMessage = 'Cannot send messages in this chat session.';
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