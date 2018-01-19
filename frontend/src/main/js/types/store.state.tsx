import { Player, Team, PlayerKey } from './types';

export interface StoreState {
  availablePlayers: Array<Player>;
  selectPlayerFor: any;
  teams: { 
    red: Team,
    blue: Team
  };
  sets: Array<{
    goals: {
      red: number,
      blue: number
    }
    offense: {
      red: PlayerKey,
      blue: PlayerKey
    }
  }>;
}

export function getEmptySet() {
  return {
    goals: { red: 0, blue: 0 },
    offense: { red: 'player1' as PlayerKey, blue: 'player1' as PlayerKey }
  };
}

export function defaultState(): StoreState {
  return {
    availablePlayers: [],
    selectPlayerFor: null,
    teams: {
      red: { player1: null, player2: null },
      blue: { player1: null, player2: null }
    },
    sets: [getEmptySet()]
  };
}