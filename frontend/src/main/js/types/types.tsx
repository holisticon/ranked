export abstract class PlayerData {
  public displayName: string;
  public imageUrl: string;
  public userName: { value: string };
}

export class Player extends PlayerData {
  public constructor(data: PlayerData) {
    super();
    Object.assign(this, data);
  }

  public get id(): string {
    return this.userName ? this.userName.value : '';
  }
}

export type PlayerKey = 'player1' | 'player2';
export type TeamColor= 'red' | 'blue';
export type TeamKey = 'team1' | 'team2';

export interface Team {
  player1: Player;
  player2: Player;
  name?: string;
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
