import * as Actions from './actions';
import { StoreState, createEmptySet, defaultState, createEmptyPlayer } from './types/store.state';
import { TeamColor, PlayerKey, Teams, Team } from './types/types';
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
      const goals = {...item.goals};
      goals[team] += offset;

      return {...item, goals: goals};
    })
  };
}

function getOtherPlayerKey(onePlayerKey: string): PlayerKey {
  return onePlayerKey === 'player1' ? 'player2' : 'player1';
}

function switchPlayerPositions(state: StoreState, teamColor: TeamColor): StoreState {
  return {
    ...state, sets: changeNthSet(state.sets, state.sets.length - 1, (item) => {
      const offense = {...item.offense};
      offense[teamColor] = getOtherPlayerKey(offense[teamColor]);
      return {...item, offense};
    })
  };
}

function removePlayerFromTeam(playerUsername: string, team: Team): Team {
  if (team.player1.username === playerUsername) {
    return { ...team, player1: createEmptyPlayer() };
  } else if (team.player2.username === playerUsername) {
    return { ...team, player2: createEmptyPlayer() };
  } else {
    return team;
  }
}

function removePlayer(playerUsername: string, teams: Teams): Teams {
  return {
    red: removePlayerFromTeam(playerUsername, teams.red),
    blue: removePlayerFromTeam(playerUsername, teams.blue)
  };
}

function startNewSet(state: StoreState): StoreState {
  const wonSets = {
    red: state.teams.red.wonSets,
    blue: state.teams.blue.wonSets
  };

  if (state.sets[state.sets.length - 1].goals.red === POINTS_PER_SET) {
    wonSets.red++;
  } else {
    wonSets.blue++;
  }

  // match ended, just refresh score and do nothing more
  if (wonSets.red === POINTS_PER_MATCH || wonSets.blue === POINTS_PER_MATCH) {
    return {
      ...state,
      teams: {
        red: {...state.teams.red, wonSets: wonSets.red},
        blue: {...state.teams.blue, wonSets: wonSets.blue}
      }
    };
  }

  // switch teams
  const newState = {
    ...state,
    teams: {
      red: {...state.teams.blue, wonSets: wonSets.blue},
      blue: {...state.teams.red, wonSets: wonSets.red}
    },
    sets: [...state.sets]
  };

  // create new empty set
  const newSet = createEmptySet();

  // switch player positions for new set
  newSet.offense.red = getOtherPlayerKey(state.sets[state.sets.length - 1].offense.red);
  newSet.offense.blue = getOtherPlayerKey(state.sets[state.sets.length - 1].offense.blue);

  // add new set
  newState.sets.push(newSet);

  return newState;
}

export function ranked(state: StoreState, rankedAction: Actions.RankedAction): StoreState {
  let action;
  switch (rankedAction.type) {
    case Actions.INC_GOALS:
      action = rankedAction as Actions.IncGoals;
      const newState = changeGoals(state, action.team, 1);

      if (newState.sets[newState.sets.length - 1].goals[action.team] >= POINTS_PER_SET) {
        return startNewSet(newState);
      }

      return newState;

    case Actions.DEC_GOALS:
      action = rankedAction as Actions.DecGoals;
      // only decrease score if above zero
      if (state.sets[state.sets.length - 1].goals[action.team] > 0) {
        return changeGoals(state, action.team, -1);
      }
      break;

    case Actions.SWITCH_PLAYER_POSITION:
      action = rankedAction as Actions.SwitchPlayerPositions;
      return switchPlayerPositions(state, action.team);

    case Actions.SELECT_PLAYER:
      action = rankedAction as Actions.SelectPlayer;
      let playerPosition = action.position;
      let currentSet = state.sets[state.sets.length - 1];
      let playerKey: PlayerKey = currentSet.offense[action.team];

      if (playerPosition === 'defense') {
        playerKey = getOtherPlayerKey(playerKey);
      }

      return {...state, selectPlayerFor: {team: action.team, position: playerKey}};

    case Actions.SET_PLAYER:
      action = rankedAction as Actions.SetPlayer;
      const teams = removePlayer(action.player.username, state.teams);
      teams[action.team][action.position] = action.player;

      return {...state, selectPlayerFor: null, teams};

    case Actions.START_NEW_MATCH:
      return defaultState();

    case Actions.UPDATE_AVAILABLE_PLAYERS:
      action = rankedAction as Actions.UpdateAvailablePlayers;

      return {...state, availablePlayers: action.players};

    default:
      break;
  }

  return state || defaultState();
}
