import * as React from 'react';
import TeamComponent from '../components/team';
import { TeamColor, Team, Sets, Teams } from '../types/types';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { POINTS_PER_MATCH } from '../config';
import { Dialog } from '../components/dialog';
import './match.css';

export interface MatchProps {
  sets: Sets;
  teams: Teams;
  setNumber: number;
  winner: TeamColor | null;
  startNewMatch: () => void;
}

function sendResults (sets: Sets, teams: Teams) {
  fetch('http://localhost:8080/command/match', {
    method: 'POST',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      teamRed: {
        player1: { value: teams.red.player1.username},
        player2: { value: teams.red.player2.username}
      },
      teamBlue: {
        player1: { value: teams.blue.player1.username},
        player2: { value: teams.blue.player2.username}
      },
      matchSets: sets.map(set => {
        return {
          type: 'result',
          goalsRed: set.goals.red,
          goalsBlue: set.goals.blue,
          offenseRed: {value: teams.red[set.offense.red].username},
          offenseBlue: {value: teams.blue[set.offense.blue].username}
        };
      })
    })
  });
}

function getWinningPlayersAsString (team: Team): string {
  if (team) {
    return `${team.player1.name} und ${team.player2.name}`;
  } else {
    return '';
  }
}

function allPlayersSet(red: Team, blue: Team): boolean {
  return !!red && !!blue && !!red.player1 && !!red.player2 && !!blue.player1 && !!blue.player2;
}

function getDialogMessage(winner: TeamColor, teams: Teams): string {
  if (allPlayersSet(teams.red, teams.blue)) {
    return 'Ganz großes Kino, ' + getWinningPlayersAsString(winner === 'red' ? teams.red : teams.blue) + '!';
  } else {
    return 'Tolles Spiel! Zum Übermitteln der Ergebnisse müssen die Spieler vorab festgelegt werden.';
  }
}

function Match({ setNumber, winner, sets, teams, startNewMatch }: MatchProps) {

  const isLastSet = setNumber === (POINTS_PER_MATCH * 2 - 1);

  return (
    <div className="match">
      {
        winner &&
        <Dialog
          headline="Spiel beendet"
          text={getDialogMessage(winner, teams)}
          buttons={ [
            { text: 'OKBÄM!', type: 'ok', click: () => {
              sendResults(sets, teams);
              startNewMatch();
            } }
          ] }
        />
      }

      <TeamComponent color={'red'} isLastSet={isLastSet}  />

      <div className="setcounter">
        <div>
          <span>{ setNumber }</span>
        </div>
      </div>

      <TeamComponent color={'blue'} isLastSet={isLastSet} />

    </div>
  );
}

export function mapStateToProps({ranked: { selectPlayerFor, teams, sets }}: any) {
  let winner: TeamColor | null = null;
  if (teams.red.wonSets === POINTS_PER_MATCH) {
    winner = 'red';
  } else if (teams.blue.wonSets === POINTS_PER_MATCH) {
    winner = 'blue';
  }

  return {
    sets,
    teams,
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
