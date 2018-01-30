import { Player, PlayerKey, Sets, Teams } from './types';

export interface StoreState {
  availablePlayers: Array<Player>;
  selectPlayerFor: any;
  teams: Teams;
  sets: Sets;
}

export function createEmptySet() {
  return {
    goals: { red: 0, blue: 0 },
    offense: { red: 'player1' as PlayerKey, blue: 'player1' as PlayerKey }
  };
}

export function createEmptyPlayer(): Player {
  return {
    name: '',
    img: '',
    username: '',
  };
}

export function defaultState(): StoreState {
  return {
    availablePlayers: [],
    selectPlayerFor: null,
    teams: {
      red: { player1: createEmptyPlayer(), player2: createEmptyPlayer(), wonSets: 0 },
      blue: { player1: createEmptyPlayer(), player2: createEmptyPlayer(), wonSets: 0 }
    },
    sets: [createEmptySet()]
  };
}
