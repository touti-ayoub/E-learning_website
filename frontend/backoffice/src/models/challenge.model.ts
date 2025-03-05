import { Badge } from './badge.model';

export interface Challenge {
    idChallenge?: number;
    name: string;
    description: string;
    createdAt: Date;
    rewardPoints: number;
    badge?: Badge;
}