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

export abstract class TeamData {
  player1: PlayerData;
  player2: PlayerData;
  name?: string;
  id?: string;
}

export class Team extends TeamData {
  player1: Player;
  player2: Player;
  wonSets: number;
  imageUrl: string;

  public constructor(data: TeamData) {
    super();
    Object.assign(this, data);

    // create correct player instances
    this.player1 = new Player(data.player1);
    this.player2 = new Player(data.player2);

    this.wonSets = 0;
  }
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

export interface PlayedMatch {
  team1: { player1: string, player2: string, goals: number };
  team2: { player1: string, player2: string, goals: number };
  winner: TeamKey;
}

export type TorunamentMatch = {
  id: number,
  team1?: Team,
  team2?: Team,
  winner?: TeamKey,
  team1Goals?: number,
  team2Goals?: number
};

export const NonPlayingTeamId = 'NPT';