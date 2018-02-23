export interface Player {
  name: string;
  imageUrl: string;
  id: string;
}

export type PlayerKey = 'player1' | 'player2';
export type TeamColor = 'red' | 'blue';
export type PlayerPosition = 'attack' | 'defense';

export interface Teams {
  red: Team;
  blue: Team;
}

export interface Team {
  player1: Player;
  player2: Player;
  wonSets: number;
}

export interface Sets extends Array<Set> {}

export interface Set {
  goals: {
    red: number,
    blue: number
  };
  offense: {
    red: PlayerKey,
    blue: PlayerKey
  };
}
