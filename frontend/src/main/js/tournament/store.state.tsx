import { Player } from '../types/types';
import { AutosaveService } from '../game/services/autosave.service';

export interface PartialStoreState {
  tournament: TournamentStoreState;
}

export interface TournamentStoreState {
  tournamentParticipants: Array<Player>;
  availablePlayers: Array<Player>;
}

export function defaultState(): TournamentStoreState {
  const saved = AutosaveService.loadStateFromLocalStorage('tournament');

  // just use the saved participants, not the saved players
  return saved || {
    tournamentParticipants: [],
    availablePlayers: []
  };
}
