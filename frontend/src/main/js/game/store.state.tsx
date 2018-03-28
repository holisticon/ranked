import { Player, PlayerKey, Sets, Set, Team, TeamKey } from '../types/types';

export interface PartialStoreState {
  ranked: RankedStore;
}

export interface RankedStore {
  availablePlayers: Array<Player>;
  selectPlayerFor: { team: TeamKey, player: PlayerKey } | null;
  team1: Team;
  team2: Team;
  sets: Sets;
}

export function createFirstSet(): Set {
  return {
    red: {
      attack: 'player1',
      defense: 'player2',
      team: 'team1',
      goals: []
    },
    blue: {
      attack: 'player1',
      defense: 'player2',
      team: 'team2',
      goals: []
    }
  };
}

export function createEmptyPlayer(): Player {
  return {
    name: '',
    imageUrl: '',
    id: '',
  };
}

function createEmptyTeam(): Team {
  return {
    player1: createEmptyPlayer(),
    player2: createEmptyPlayer(),
    wonSets: 0
  };
}

export function defaultState(): RankedStore {
  return {
    availablePlayers: [],
    selectPlayerFor: null,
    team1: createEmptyTeam(),
    team2: createEmptyTeam(),
    sets: [createFirstSet()]
  };
}
