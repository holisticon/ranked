import { Player } from '../types/types';

export interface PartialStoreState {
  tournament: TournamentStoreState;
}

export interface TournamentStoreState {
  tournamentParticipants: Array<Player>;
  availablePlayers: Array<Player>;
}

export function defaultState(): TournamentStoreState {
  return {
    tournamentParticipants: [],
    availablePlayers: []
  };
}
