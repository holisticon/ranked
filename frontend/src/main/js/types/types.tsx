export interface Player {
  name: string;
  imageUrl: string;
  id: string;
}

export type PlayerKey = 'player1' | 'player2';
export type TeamColor= 'red' | 'blue';
export type TeamKey = 'team1' | 'team2';

export interface Team {
  player1: Player;
  player2: Player;
  wonSets: number;
}

export type Sets = Array<Set>;

export interface Set {
  red: Composition;
  blue: Composition;
}

export interface Composition {
  attack: PlayerKey;
  defense: PlayerKey;
  team: TeamKey;
  goals: number;
}
