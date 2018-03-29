import * as Actions from './actions';
import { RankedStore, defaultState, createEmptyPlayer } from './store.state';
import { TeamColor, Team, Composition, TeamKey } from '../types/types';
import { POINTS_PER_SET, POINTS_PER_MATCH } from '../config';
import { TimerService } from './services/timer.service';

function copyAndSet<T>(item: any, setter: (itemCopy: T) => void): T {
  const copy: T = {...item};
  setter(copy);

  return copy;
}

function changeNthSet<T>(sets: Array<T>, n: number, itemChanger: (item: T) => T): Array<T> {
  return sets.map((item, index) => {
    if (index === n) {
      return itemChanger(item);
    } else {
      return item;
    }
  });
}

function changeGoals(state: RankedStore, color: TeamColor, offset: number): RankedStore {
  return {
    ...state,
    sets: changeNthSet(state.sets, state.sets.length - 1, (item) => {
      const team: Composition = copyAndSet(item[color], newTeam => {
        // remove goals if offset is negative
        let newGoals = newTeam.goals.slice(0, newTeam.goals.length + offset);

        // push new elements if offset is positive
        newGoals.push(...Array(Math.max(offset, 0)).fill(TimerService.getTimeInSec()));

        newTeam.goals = newGoals;
      });

      return copyAndSet(item, newItem => newItem[color] = team);
    })
  };
}

function switchPlayerPositions(state: RankedStore, teamColor: TeamColor): RankedStore {
  return {
    ...state, sets: changeNthSet(state.sets, state.sets.length - 1, (item) => {
      return copyAndSet(item, newItem => {
        newItem[teamColor] = {
          ...item[teamColor],
          attack: item[teamColor].defense,
          defense: item[teamColor].attack
        };
      });
    })
  };
}

function removePlayerFromTeam(playerUsername: string, team: Team): Team {
  if (team.player1.id === playerUsername) {
    return { ...team, player1: createEmptyPlayer() };
  } else if (team.player2.id === playerUsername) {
    return { ...team, player2: createEmptyPlayer() };
  } else {
    return team;
  }
}

function startNewSet(state: RankedStore): RankedStore {
  const currentSet = state.sets[state.sets.length - 1];
  let winnerTeam: TeamKey;

  if (currentSet.red.goals.length === POINTS_PER_SET) {
    winnerTeam = currentSet.red.team;
  } else {
    winnerTeam = currentSet.blue.team;
  }
  const refreshedWinnerTeam: Team = copyAndSet(state[winnerTeam], copy => copy.wonSets += 1);
  
  return copyAndSet(state, newState => {
    newState[winnerTeam] = refreshedWinnerTeam;
    if (refreshedWinnerTeam.wonSets < POINTS_PER_MATCH) {
      // match not ended, add next set to match
      // switch teams and player positions
      newState.sets.push({
        red: {
          attack: currentSet.blue.defense,
          defense: currentSet.blue.attack,
          team: currentSet.blue.team,
          goals: []
        },
        blue: {
          attack: currentSet.red.defense,
          defense: currentSet.red.attack,
          team: currentSet.red.team,
          goals: []
        }
      });
    }
  });
}

export function ranked(state: RankedStore, rankedAction: Actions.RankedAction): RankedStore {
  let action;
  switch (rankedAction.type) {
    case Actions.INC_GOALS:
      action = rankedAction as Actions.IncGoals;
      const newState = changeGoals(state, action.team, 1);

      if (newState.sets[newState.sets.length - 1][action.team].goals.length >= POINTS_PER_SET) {
        return startNewSet(newState);
      }

      // Start timer whenever a goal is scored to be sure it's running ;)
      TimerService.start();

      return newState;

    case Actions.DEC_GOALS:
      action = rankedAction as Actions.DecGoals;
      // only decrease score if above zero
      if (state.sets[state.sets.length - 1][action.team].goals.length > 0) {
        return changeGoals(state, action.team, -1);
      }
      break;

    case Actions.SWITCH_PLAYER_POSITION:
      action = rankedAction as Actions.SwitchPlayerPositions;
      return switchPlayerPositions(state, action.team);

    case Actions.SELECT_PLAYER:
      action = rankedAction as Actions.SelectPlayer;

      return {...state, selectPlayerFor: {team: action.team, player: action.player}};

    case Actions.SET_PLAYER:
      const setPlayerAction = rankedAction as Actions.SetPlayer;

      return copyAndSet(state, copyState => {
        copyState.selectPlayerFor = null;

        // remove selected player from everywhere if it was already selected
        copyState.team1 = removePlayerFromTeam(setPlayerAction.selected.id, copyState.team1);
        copyState.team2 = removePlayerFromTeam(setPlayerAction.selected.id, copyState.team2);

        // add selected player
        copyState[setPlayerAction.team][setPlayerAction.player] = setPlayerAction.selected;
      });

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
