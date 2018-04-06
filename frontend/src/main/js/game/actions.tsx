import { Player, TeamColor, PlayerKey, TeamKey, Team } from '../types/types';

export const INC_GOALS = 'INC_GOALS';
export const DEC_GOALS = 'DEC_GOALS';
export const SELECT_PLAYER = 'SELECT_PLAYER';
export const SET_PLAYER = 'SET_PLAYER';
export const SWITCH_PLAYER_POSITION = 'SWITCH_PLAYER_POSITION';
export const START_NEW_MATCH = 'START_NEW_MATCH';
export const UPDATE_AVAILABLE_PLAYERS = 'UPDATE_AVAILABLE_PLAYERS';
export const UPDATE_AVAILABLE_TEAMS = 'UPDATE_AVAILABLE_TEAMS';

export type RankedAction = IncGoals | DecGoals | SelectPlayer | SetPlayer | SwitchPlayerPositions | StartNewMatch;

export interface IncGoals {
  type: string;
  team: TeamColor;
}

export interface DecGoals {
  type: string;
  team: TeamColor;
}

export interface SelectPlayer {
  type: string;
  team: TeamKey;
  player: PlayerKey;
}

export interface SetPlayer {
  type: string;
  team: TeamKey;
  player: PlayerKey;
  selected: Player;
}

export interface SwitchPlayerPositions {
  type: string;
  team: TeamColor;
}

export interface StartNewMatch {
  type: string;
}

export interface UpdateAvailablePlayers {
  type: string;
  players: Array<Player>;
}

export interface UpdateAvailableTeams {
  type: string;
  teams: Array<Team>;
}

export function incGoals(team: TeamColor): IncGoals {
  return {
      type: INC_GOALS,
      team
  };
}

export function decGoals(team: TeamColor): DecGoals {
  return {
      type: DEC_GOALS,
      team
  };
}

export function selectPlayer(team: TeamKey, player: PlayerKey): SelectPlayer {
  return {
    type: SELECT_PLAYER,
    team,
    player
  };
}

export function setPlayer(team: TeamKey, player: PlayerKey, selected: Player): SetPlayer {
  return {
    type: SET_PLAYER,
    team,
    player,
    selected
  };
}

export function switchPlayerPositions(team: TeamColor): SwitchPlayerPositions {
  return {
    type: SWITCH_PLAYER_POSITION,
    team
  };
}

export function startNewMatch(): StartNewMatch {
  return {
    type: START_NEW_MATCH
  };
}

export function updateAvailablePlayers(players: Array<Player>): UpdateAvailablePlayers {
  return {
    type: UPDATE_AVAILABLE_PLAYERS,
    players
  };
}

export function updateAvailableTeams(teams: Array<Team>): UpdateAvailableTeams {
  return {
    type: UPDATE_AVAILABLE_TEAMS,
    teams
  };
}
