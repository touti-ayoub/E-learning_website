export interface Badge {
    id: number;
    name: string;
    description: string;
  }
  
  export interface Challenge {
    id: number;
    title: string;
    description: string;
    points: number;
  }
  
  export interface Point {
    id: number;
    userId: number;
    challengeId: number;
    pointsEarned: number;
  }
  
  export interface UserChallenge {
    id: number;
    userId: number;
    challengeId: number;
    status: string;
  }
  