export interface Challenge {
    idChallenge: number;
    name: string;
    description: string;
    createdAt: string;
    rewardPoints: number;
    badge?: Badge;
  }
  
  export interface Badge {
    idBadge: number;
    name: string;
    iconUrl: string;
  }