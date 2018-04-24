export namespace BackendData {
  export interface UserName {
    value: string;
  }

  export interface Team {
    player1: UserName;
    player2: UserName;
  }

  export interface MatchSet {
    goalsBlue: number;
    goalsRed: number;
    offenseBlue: number;
    offenseRed: number;
  }

  export interface Match {
    date: string;
    matchId: string;
    matchSets: Array<MatchSet>;
    teamBlue: Team;
    teamRed: Team;
  }
}