export class Player {
  displayName: string;
  imageUrl: string;
  userName: { value: string };
  get id(): string {
    return this.userName ? this.userName.value : '';
  }
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
  goals: Array<number>;
}
