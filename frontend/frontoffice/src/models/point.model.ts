
import { Challenge } from './Challenge.model';

export interface Point {
  id: number;
  dateObtention: string;
  challenge?: { name: string }; // Vérifiez que cette ligne est correcte
  pointWins: number;
  typeActivity: string;
}