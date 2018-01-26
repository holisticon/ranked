export interface Player {
  name: string;
  img: string;
  username: string;
}

export type PlayerKey = 'player1' | 'player2';
export type TeamColor = 'red' | 'blue';
export type PlayerPostion = 'attack' | 'defense';

export interface Team {
  player1: Player | null;
  player2: Player | null;
  wonSets: number;
}

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

export interface Sets extends Array<Set> {}

export interface Teams {
  red: Team;
  blue: Team;
}
