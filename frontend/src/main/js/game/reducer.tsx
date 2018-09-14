import { Config } from '../config';
import { Composition, Player, Team, TeamColor, TeamKey } from '../types/types';
import * as Actions from './actions';
import { TimerService } from './services/timer.service';
import { createEmptyPlayer, createEmptyTeam, defaultState, RankedStore } from './store.state';

function copyAndSet<T>(item: any, setter: (itemCopy: T) => void): T {
  const copy: T = { ...item };
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

function removeTeamIfPresent(teamId: string | undefined, team: Team): Team {
  if (team.id === teamId) {
    return createEmptyTeam();
  } else {
    return team;
  }
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

  if (currentSet.red.goals.length > currentSet.blue.goals.length) {
    winnerTeam = currentSet.red.team;
  } else {
    winnerTeam = currentSet.blue.team;
  }
  const refreshedWinnerTeam: Team = copyAndSet(state[winnerTeam], copy => copy.wonSets += 1);

  return copyAndSet(state, newState => {
    newState[winnerTeam] = refreshedWinnerTeam;
    if (refreshedWinnerTeam.wonSets < Config.pointsPerMatch) {
      // match not ended, add next set to match
      // switch teams and player positions
      newState.suddenDeathMode = false;
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
    case Actions.LOAD_STATE:
      return (rankedAction as Actions.LoadState).state;

    case Actions.INC_GOALS:
      action = rankedAction as Actions.IncGoals;
      const newState = changeGoals(state, action.team, 1);

      if (newState.suddenDeathMode ||
        (!Config.timedMatchMode &&
          newState.sets[newState.sets.length - 1][action.team].goals.length >= Config.pointsPerSet)
      ) {
        if (newState.suddenDeathMode) {
          TimerService.pause();
          TimerService.resetCountdown();
        }
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

    case Actions.SELECT_ENTITY:
      action = rankedAction as Actions.SelectEntity;

      return { ...state, selectFor: { team: action.team, player: action.player } };

    case Actions.SET_PLAYER:
      const setPlayerAction = rankedAction as Actions.SetPlayer;

      return copyAndSet(state, copyState => {
        copyState.selectFor = null;
        const selectedPlayer = new Player(setPlayerAction.selected);

        // remove selected player from everywhere if it was already selected
        copyState.team1 = removePlayerFromTeam(selectedPlayer.id, copyState.team1);
        copyState.team2 = removePlayerFromTeam(selectedPlayer.id, copyState.team2);

        // add selected player
        copyState[setPlayerAction.team][setPlayerAction.player] = new Player(selectedPlayer);
      });

    case Actions.SET_TEAM:
      const setTeamAction = rankedAction as Actions.SetTeam;

      return copyAndSet(state, copyState => {
        copyState.selectFor = null;

        copyState.team1 = removeTeamIfPresent(setTeamAction.selected.id, copyState.team1);
        copyState.team2 = removeTeamIfPresent(setTeamAction.selected.id, copyState.team2);

        // set selected team
        copyState[setTeamAction.team] = setTeamAction.selected;
      });

    case Actions.START_NEW_MATCH:
      TimerService.reset();
      return { ...defaultState(), devicePosition: state.devicePosition };

    case Actions.UPDATE_AVAILABLE_PLAYERS:
      action = rankedAction as Actions.UpdateAvailablePlayers;

      return { ...state, availablePlayers: action.players };

    case Actions.UPDATE_AVAILABLE_TEAMS:
      action = rankedAction as Actions.UpdateAvailableTeams;

      return { ...state, availableTeams: action.teams };

    case Actions.COUNTDOWN_EXPIRED:
      const currenSet = state.sets[state.sets.length - 1];
      if (currenSet.blue.goals.length !== currenSet.red.goals.length) {
        TimerService.pause();
        TimerService.resetCountdown();
        return startNewSet(state);
      } else {
        return { ...state, suddenDeathMode: true };
      }

    case Actions.RESUME_MATCH:
      TimerService.setTime((rankedAction as Actions.StartTimer).currentTimerTime);
      TimerService.start();
      break;

    case Actions.PAUSE_MATCH:
      TimerService.setTime((rankedAction as Actions.PauseTimer).currentTimerTime);
      TimerService.pause();
      break;

    case Actions.SET_DEVICE_POSITION:
      action = rankedAction as Actions.SetDevicePosition;

      return { ...state, devicePosition: action.position };

    default:
      break;
  }

  return state || defaultState();
}
