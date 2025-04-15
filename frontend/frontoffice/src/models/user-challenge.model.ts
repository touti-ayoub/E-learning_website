import { Challenge } from './Challenge.model';
import { User } from './user.model';

export interface UserChallenge {
  id: number;
  user: User;
  challenge: Challenge;
  completed: boolean;
}