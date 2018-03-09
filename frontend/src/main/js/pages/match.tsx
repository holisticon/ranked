import * as React from 'react';
import TeamComponent from '../components/team';
import { Sets, Team, TeamKey, Set, Composition } from '../types/types';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import axios from 'axios';
import { POINTS_PER_MATCH } from '../config';
import { Dialog } from '../components/dialog';
import './match.css';
import { PartialStoreState } from '../types/store.state';
import PanelComponent from '../components/panel';
import { Timer } from '../misc/timer.service';

export interface MatchProps {
  sets: Sets;
  team1: Team;
  team2: Team;
  setNumber: number;
  winner: TeamKey | null;
  startNewMatch: () => void;
}

function getTeam(set: Set, team: TeamKey): Composition {
  return set.red.team === team ? set.red : set.blue;
}

function mapGoalsForBackend(teamRedGoals: Array<number>, teamBlueGoals: Array<number>, startTime?: Date) {
  let timestamps = teamRedGoals.every(t => t >= 0) && teamBlueGoals.every(t => t >= 0);

  if (startTime !== undefined && timestamps) {
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
  const matchTime = Timer.Service.getTimeInSec();
  let startTime: Date | undefined = undefined;

  if (matchTime > 0) {
    let now = new Date();
    startTime = new Date(now.getTime() - (Timer.Service.getTimeInSec() * 1000));
  }
  Timer.Service.reset();

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

function getWinningPlayersAsString(team: Team): string {
  if (team) {
    return `${team.player1.name} und ${team.player2.name}`;
  } else {
    return '';
  }
}

function allPlayersSet(team1: Team, team2: Team): boolean {
  return !!team1 && !!team2 && !!team1.player1.id && !!team1.player2.id && !!team2.player1.id && !!team2.player2.id;
}

function getDialogMessage(winner: TeamKey, team1: Team, team2: Team): string {
  if (allPlayersSet(team1, team2)) {
    return 'Ganz großes Kino, ' + getWinningPlayersAsString(winner === 'team1' ? team1 : team2) + '!';
  } else {
    return 'Tolles Spiel! Zum Übermitteln der Ergebnisse müssen die Spieler vorab festgelegt werden.';
  }
}

function Match({ setNumber, winner, sets, team1, team2, startNewMatch }: MatchProps) {
  const isLastSet = setNumber === (POINTS_PER_MATCH * 2 - 1);

  if (winner) {
    Timer.Service.pause();
  }

  return (
    <div className="match">
      {
        winner &&
        <Dialog
          headline="Spiel beendet"
          text={getDialogMessage(winner, team1, team2)}
          buttons={[
            {
              text: 'OKBÄM!', type: 'ok', click: () => {
                sendResults(sets, team1, team2);
                startNewMatch();
              }
            }
          ]}
        />
      }

      <TeamComponent color={'red'} isLastSet={isLastSet} />

      <div className="setcounter">
        <div>
          <span>{setNumber}</span>
        </div>
      </div>

      <TeamComponent color={'blue'} isLastSet={isLastSet} />

      <PanelComponent />

    </div>
  );
}

export function mapStateToProps({ ranked: { selectPlayerFor, team1, team2, sets } }: PartialStoreState) {
  let winner: TeamKey | null = null;
  if (team1.wonSets === POINTS_PER_MATCH) {
    winner = 'team1';
  } else if (team2.wonSets === POINTS_PER_MATCH) {
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
    startNewMatch: () => dispatch(Actions.startNewMatch())
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Match);
