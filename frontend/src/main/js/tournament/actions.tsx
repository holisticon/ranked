import { Player } from '../types/types';

export const ADD_PARTICIPANT = 'ADD_PARTICIPANT';
export const REMOVE_PARTICIPANT = 'REMOVE_PARTICIPANT';
export const UPDATE_PLAYERS = 'UPDATE_PLAYERS';

export type TournamentAction = UpdatePlayers | AddParticipant | RemoveParticipant;

export interface AddParticipant {
  type: string;
  player: Player;
}

export interface RemoveParticipant {
  type: string;
  player: Player;
}

export interface UpdatePlayers {
  type: string;
  players: Array<Player>;
}

export function addParticipant(player: Player): AddParticipant {
  return {
      type: ADD_PARTICIPANT,
      player
  };
}

export function removeParticipant(player: Player): RemoveParticipant {
  return {
      type: REMOVE_PARTICIPANT,
      player
  };
}

export function updatePlayers(players: Array<Player>): UpdatePlayers {
  return {
      type: UPDATE_PLAYERS,
      players
  };
}
