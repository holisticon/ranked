import { Player, PlayerKey, Sets, Set, Team, TeamKey, TeamColor } from '../types/types';

export interface PartialStoreState {
  ranked: RankedStore;
}

export interface RankedStore {
  availablePlayers: Array<Player>;
  availableTeams: Array<Team>;
  selectFor: { team: TeamKey, player?: PlayerKey } | null;
  team1: Team;
  team2: Team;
  sets: Sets;
  suddenDeathMode: boolean;
  devicePosition: TeamColor | null;
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
  return new Player({ displayName: '', imageUrl: '', userName: { value: '' } });
}

export function createEmptyTeam(): Team {
  return {
    player1: createEmptyPlayer(),
    player2: createEmptyPlayer(),
    wonSets: 0,
    imageUrl: ''
  };
}

export function defaultState(): RankedStore {
  return {
    availablePlayers: [],
    availableTeams: [],
    selectFor: null,
    team1: createEmptyTeam(),
    team2: createEmptyTeam(),
    sets: [createFirstSet()],
    suddenDeathMode: false,
    devicePosition: null
  };
}
