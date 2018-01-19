import * as Actions from './actions';
import { StoreState, getEmptySet } from './types/store.state';
import { TeamColor, PlayerKey } from './types/types';
import { POINTS_PER_SET, POINTS_PER_MATCH } from './config';

function changeNthSet<T>(sets: Array<T>, n: number, itemChanger: (item: T) => T): Array<T> {
  return sets.map((item, index) => {
    if (index === n) {
      return itemChanger(item);
    } else {
      return item;
    }
  });
}

function changeGoals(state: StoreState, team: TeamColor, offset: number): StoreState {
  return {
    ...state, sets: changeNthSet(state.sets, state.sets.length - 1, (item) => {
      const goals = { ...item.goals };
      goals[team] += offset;

      return { ...item, goals: goals };
    })
  };
}

function getOtherPlayerKey(onePlayerKey: string): PlayerKey {
  return onePlayerKey === 'player1' ? 'player2' : 'player1';
}

function switchPlayerPositions(state: StoreState, teamColor: TeamColor): StoreState {
  return {
    ...state, sets: changeNthSet(state.sets, state.sets.length - 1, (item) => {
      const offense = { ...item.offense };
      offense[teamColor] = getOtherPlayerKey(offense[teamColor]);
      return { ...item, offense };
    })
  };
}

function startNewSet(state: StoreState): StoreState {
  const wonSets = { red: state.teams.red.wonSets, blue: state.teams.blue.wonSets };
  
  if (state.sets[state.sets.length - 1].goals.red === POINTS_PER_SET) {
    wonSets.red++;
  } else {
    wonSets.blue++;
  }

  if (wonSets.red === POINTS_PER_MATCH || wonSets.blue === POINTS_PER_MATCH) {
    endMatch();
    return state;
  }

  const newState = {
    ...state,
    teams: {
      red: { ...state.teams.blue, wonSets: wonSets.blue },
      blue: { ...state.teams.red, wonSets: wonSets.red }
    },
    sets: [...state.sets]
  };

  // create new empty set
  const newSet = getEmptySet();

  // switch player positions for new set
  newSet.offense.red = getOtherPlayerKey(state.sets[state.sets.length - 1].offense.red);
  newSet.offense.blue = getOtherPlayerKey(state.sets[state.sets.length - 1].offense.blue);

  // add new set
  newState.sets.push(newSet);

  return newState;
}

function endMatch(): void {
  alert('Spiel ist beendet!');
}

export function rankedReducer(state: StoreState, action: Actions.RankedAction): StoreState {
  switch (action.type) {
    case Actions.INC_GOALS:
      const newState = changeGoals(state, action.team, 1);

      if (newState.sets[newState.sets.length - 1].goals[action.team] >= POINTS_PER_SET) {
        return startNewSet(newState);
      }

      return newState;

    case Actions.DEC_GOALS:
      // only decrease score if above zero
      if (state.sets[state.sets.length - 1].goals[action.team] > 0) {
        return changeGoals(state, action.team, -1);
      }
      break;

    case Actions.SWITCH_PLAYER_POSITION:
      return switchPlayerPositions(state, action.team);

    case Actions.SELECT_PLAYER:
      let playerPosition = (action as Actions.SelectPlayer).position;
      let currentSet = state.sets[state.sets.length - 1];
      let playerKey: PlayerKey = currentSet.offense[action.team];

      if (playerPosition === 'defense') {
        playerKey = getOtherPlayerKey(playerKey);
      }

      return { ...state, selectPlayerFor: { team: action.team, position: playerKey } };

    case Actions.SET_PLAYER:
      const a = action as Actions.SetPlayer;
      const teams = { ...state.teams };

      teams[a.team] = { ...state.teams[a.team] };
      teams[a.team][a.position] = a.player;

      return { ...state, selectPlayerFor: null, teams };

    default:
      break;
  }

  return state;
}