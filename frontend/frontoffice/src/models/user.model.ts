
export interface User {
    id: number;
    username: string;
    password?: string; // Optional in frontend
    role: string;
}