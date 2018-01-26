export interface Player {
  name: string;
  img: string;
}

export type PlayerKey = 'player1' | 'player2';
export type TeamColor = 'red' | 'blue';
export type PlayerPostion = 'attack' | 'defense';

export interface Team {
  player1: Player | null;
  player2: Player | null;
  wonSets: number;
}