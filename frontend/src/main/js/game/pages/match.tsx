import * as React from 'react';
import TeamComponent from '../components/team';
import { Sets, Team, TeamKey, Set, Composition } from '../../types/types';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import axios from 'axios';
import { Dialog } from '../../components/dialog';
import './match.css';
import { PartialStoreState, RankedStore } from '../store.state';
import PanelComponent from '../components/panel';
import { TimerService } from '../services/timer.service';
import { Config } from '../../config';
import { push } from 'react-router-redux';
import { AutosaveService } from '../services/autosave.service';

export interface MatchProps {
  sets: Sets;
  team1: Team;
  team2: Team;
  setNumber: number;
  winner: TeamKey | null;
  startNewMatch: () => void;
  routeBack: () => void;
  loadState: (state: RankedStore) => void;
}

function getTeam(set: Set, team: TeamKey): Composition {
  return set.red.team === team ? set.red : set.blue;
}

function mapGoalsForBackend(teamRedGoals: Array<number>, teamBlueGoals: Array<number>, startTime?: Date) {
  if (startTime !== undefined) {
    let goals = [];

    // merge both goal arrays together ...
    goals.push(...teamRedGoals.map(sec => {
      return { team: 'RED', time: sec };
    }));
    goals.push(...teamBlueGoals.map(sec => {
      return { team: 'BLUE', time: sec };
    }));

    // ... and sort them
    goals.sort((a, b) => a.time - b.time);

    return {
      type: 'timestamp',
      goals: goals.map(timedGoal => {
        return {
          first: timedGoal.team,
          second: new Date(startTime.getTime() + timedGoal.time * 1000)
        };
      })
    };
  } else {
    return {
      type: 'result',
      goalsRed: teamRedGoals.length,
      goalsBlue: teamBlueGoals.length,
    };
  }
}

function sendResults(sets: Sets, team1: Team, team2: Team) {
  const matchTime = TimerService.getTimeInSec();
  let startTime: Date | undefined = undefined;

  if (matchTime > 0) {
    let now = new Date();
    startTime = new Date(now.getTime() - (TimerService.getTimeInSec() * 1000));
  }
  TimerService.reset();

  axios.post('command/match', {
    teamRed: {
      player1: { value: team1.player1.id },
      player2: { value: team1.player2.id }
    },
    teamBlue: {
      player1: { value: team2.player1.id },
      player2: { value: team2.player2.id }
    },
    matchSets: sets.map(set => {
      return {
        ...mapGoalsForBackend(getTeam(set, 'team1').goals, getTeam(set, 'team2').goals, startTime),
        offenseRed: { value: team1[getTeam(set, 'team1').attack].id },
        offenseBlue: { value: team2[getTeam(set, 'team2').attack].id }
      };
    }),
    startTime
  });
}

function getMatchWinnersAsString(team: Team): string | undefined {
  if (team) {
    if (Config.teamMode) {
      return `Team ${team.name}`;
    } else {
      return `${team.player1.displayName} und ${team.player2.displayName}`;
    }
  } else {
    return '';
  }
}

function allPlayersSet(team1: Team, team2: Team): boolean {
  return !!team1 && !!team2 && !!team1.player1.id && !!team1.player2.id && !!team2.player1.id && !!team2.player2.id;
}

function getDialogMessage(winner: TeamKey, team1: Team, team2: Team): string {
  if (allPlayersSet(team1, team2)) {
      return 'Ganz großes Kino, ' + getMatchWinnersAsString(winner === 'team1' ? team1 : team2) + '!' + 
             ' Das Spielergebnis wird jetzt übermittelt.';
  } else {
    return 'Tolles Spiel! Zum Übermitteln der Ergebnisse müssen die Spieler vorab festgelegt werden.';
  }
}

function Match({ setNumber, winner, sets, team1, team2, startNewMatch, routeBack, loadState }: MatchProps) {

  if (winner) {
    TimerService.pause();
  }

  return (
    <div className="match">
      {
        winner &&
        <Dialog
          headline="Spiel beendet"
          text={ getDialogMessage(winner, team1, team2) }
          buttons={[
            {
              text: 'OK!', type: 'ok', click: () => {
                sendResults(sets, team1, team2);
                startNewMatch();
              }
            },
            {
              text: 'Korrektur', type: 'warn', click: () => {
                loadState(AutosaveService.getLastState());
              }
            }
          ]}
        />
      }

      <TeamComponent color={'red'} />

      <div className={'setcounter' + (Config.pointsPerMatch > 1 ? '' : ' hidden')}>
        <div>
          <span>{setNumber}</span>
        </div>
      </div>

      <TeamComponent color={'blue'} />

      <PanelComponent />

    </div>
  );
}

export function mapStateToProps({ ranked: { selectFor, team1, team2, sets } }: PartialStoreState) {
  let winner: TeamKey | null = null;
  if (team1.wonSets === Config.pointsPerMatch) {
    winner = 'team1';
  } else if (team2.wonSets === Config.pointsPerMatch) {
    winner = 'team2';
  }

  return {
    sets,
    team1,
    team2,
    setNumber: sets.length,
    winner
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
  return {
    startNewMatch: () => dispatch(Actions.startNewMatch()),
    routeBack: () => dispatch(push('/selectMatch')),
    loadState: (state: RankedStore) => dispatch(Actions.loadState(state))
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Match);
