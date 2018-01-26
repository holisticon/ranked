import { Player, TeamColor, PlayerPostion, PlayerKey } from './types/types';

export const INC_GOALS = 'INC_GOALS';
export const DEC_GOALS = 'DEC_GOALS';
export const SELECT_PLAYER = 'SELECT_PLAYER';
export const SET_PLAYER = 'SET_PLAYER';
export const SWITCH_PLAYER_POSITION = 'SWITCH_PLAYER_POSITION';
export const START_NEW_MATCH = 'START_NEW_MATCH';
export const UPDATE_AVAILABLE_PLAYERS = 'UPDATE_AVAILABLE_PLAYERS';

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
  team: TeamColor;
  position: PlayerPostion;
}

export interface SetPlayer {
  type: string;
  team: TeamColor;
  position: PlayerKey;
  player: Player;
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

export function selectPlayer(team: TeamColor, position: PlayerPostion): SelectPlayer {
  return {
    type: SELECT_PLAYER,
    team,
    position
  };
}

export function setPlayer(team: TeamColor, position: PlayerKey, player: Player): SetPlayer {
  return {
    type: SET_PLAYER,
    team,
    position,
    player
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
