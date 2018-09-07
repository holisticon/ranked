import { Player, PlayerKey, Team, TeamColor, TeamKey } from '../types/types';
import { RankedStore } from './store.state';

export const INC_GOALS = 'INC_GOALS';
export const DEC_GOALS = 'DEC_GOALS';
export const SELECT_ENTITY = 'SELECT_ENTITY';
export const SET_PLAYER = 'SET_PLAYER';
export const SET_TEAM = 'SET_TEAM';
export const SWITCH_PLAYER_POSITION = 'SWITCH_PLAYER_POSITION';
export const START_NEW_MATCH = 'START_NEW_MATCH';
export const UPDATE_AVAILABLE_PLAYERS = 'UPDATE_AVAILABLE_PLAYERS';
export const UPDATE_AVAILABLE_TEAMS = 'UPDATE_AVAILABLE_TEAMS';
export const COUNTDOWN_EXPIRED = 'COUNTDOWN_EXPIRED';
export const LOAD_STATE = 'LOAD_STATE';
export const RESUME_MATCH = 'RESUME_MATCH';
export const PAUSE_MATCH = 'PAUSE_MATCH';

export type RankedAction =
  IncGoals | DecGoals | SelectEntity | SetPlayer | SetTeam | SwitchPlayerPositions | StartNewMatch |
  CountdownExpired | StartTimer | PauseTimer;

export interface IncGoals {
  type: string;
  team: TeamColor;
  player: string;
  manikin: string;
  time: number;
}

export interface DecGoals {
  type: string;
  team: TeamColor;
}

export interface SelectEntity {
  type: string;
  team: TeamKey;
  player?: PlayerKey;
}

export interface SetPlayer {
  type: string;
  team: TeamKey;
  player: PlayerKey;
  selected: Player;
}

export interface SetTeam {
  type: string;
  team: TeamKey;
  selected: Team;
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

export interface CountdownExpired {
  type: string;
}

export interface LoadState {
  type: string;
  state: RankedStore;
}

export interface StartTimer {
  type: string;
  currentTimerTime: number;
}

export interface PauseTimer {
  type: string;
  currentTimerTime: number;
}

export function incGoals(team: TeamColor, player: string, manikin: string, time: number): IncGoals {
  return {
    type: INC_GOALS,
    team,
    player,
    manikin,
    time
  };
}

export function decGoals(team: TeamColor): DecGoals {
  return {
    type: DEC_GOALS,
    team
  };
}

export function selectEntity(team: TeamKey, player?: PlayerKey): SelectEntity {
  return {
    type: SELECT_ENTITY,
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

export function setTeam(team: TeamKey, selected: Team): SetTeam {
  return {
    type: SET_TEAM,
    team,
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

export function countdownExpired(): CountdownExpired {
  return {
    type: COUNTDOWN_EXPIRED
  };
}

export function startTimer(currentTimerTime: number): StartTimer {
  return {
    type: RESUME_MATCH,
    currentTimerTime
  };
}

export function pauseTimer(currentTimerTime: number): PauseTimer {
  return {
    type: PAUSE_MATCH,
    currentTimerTime
  };
}

export function loadState(state: RankedStore): LoadState {
  return {
    type: LOAD_STATE,
    state
  };
}
