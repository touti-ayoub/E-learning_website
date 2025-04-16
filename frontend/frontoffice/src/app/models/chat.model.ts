export interface ChatMessage {
    id?: number;
    userId: number;
    role: 'user' | 'assistant';
    content: string;
    timestamp: Date;
    chatSessionId: number;
}

export interface ChatSession {
    id?: number;
    userId: number;
    title: string;
    createdAt: Date;
    updatedAt: Date;
    messages: ChatMessage[];
} 