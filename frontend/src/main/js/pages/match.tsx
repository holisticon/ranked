import * as React from 'react';
import { Swipeable } from 'react-touch';
import { PlayerIcon } from '../components/player_icon';
import { TeamColor, PlayerPostion, Player, Team, PlayerKey, Sets, Teams } from '../types/types';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { POINTS_PER_MATCH } from '../config';
import { Dialog } from '../components/dialog';
import { push } from 'react-router-redux';
import './match.css';

interface ActiveTeam {
  goals: number;
  attack: Player;
  defense: Player;
}

export interface MatchProps {
  sets: Sets;
  teams: Teams;
  selectPlayerFor: {team: TeamColor, position: PlayerKey};
  setNumber: number;
  red: ActiveTeam;
  blue: ActiveTeam;
  winner: TeamColor | null;
  incGoals: (team: TeamColor) => void;
  decGoals: (team: TeamColor) => void;
  selectPlayer: (team: TeamColor, position: PlayerPostion) => void;
  setPlayer: (team: TeamColor, position: PlayerKey, player: Player) => void;
  switchPlayerPositions: (team: TeamColor) => void;
  startNewMatch: () => void;
}

// helper functions
function stopEvent(event: React.SyntheticEvent<Object>): boolean {
  event.preventDefault();
  event.stopPropagation();
  return true;
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

function getWinningPlayersAsString (team: ActiveTeam): string {
  if (team) {
    return `${team.defense.name} und ${team.attack.name}`;
  } else {
    return '';
  }
}

function allPlayersSet(red: ActiveTeam, blue: ActiveTeam): boolean {
  return !!red && !!blue && !!red.attack && !!red.defense && !!blue.attack && !!blue.defense;
}

function getDialogMessage(winner: TeamColor, red: ActiveTeam, blue: ActiveTeam): string {
  if (allPlayersSet(red, blue)) {
    return 'Ganz großes Kino, ' + getWinningPlayersAsString(winner === 'red' ? red : blue) + '!';
  } else {
    return 'Tolles Spiel! Zum Übermitteln der Ergebnisse müssen die Spieler vorab festgelegt werden.';
  }
}

function Match({ selectPlayerFor, setNumber, red, blue, incGoals, winner,
  decGoals, selectPlayer, setPlayer, switchPlayerPositions, startNewMatch, sets, teams }: MatchProps) {

  const isLastSet = setNumber === (POINTS_PER_MATCH * 2 - 1);

  return (
    <div className="match">
      {
        winner &&
        <Dialog
          headline="Spiel beendet"
          text={getDialogMessage(winner, red, blue)}
          buttons={ [
            { text: 'OKBÄM!', type: 'ok', click: () => {
              sendResults(sets, teams);
              startNewMatch();
            } }
          ] }
        />
      }

      <div className="team-red" onClick={ () => incGoals('red') }>
        <div className="goal-counter-container">
          <Swipeable onSwipeRight={ () => incGoals('red') } onSwipeLeft={ () => decGoals('red') }>
            <div className="goal-counter">
              <span className="current-goals">{ red.goals }</span>
            </div>
          </Swipeable>
        </div>

        <div className="add-defense" onClick={ (e) => stopEvent(e) && selectPlayer('red', 'defense') }>
          {
            !red.defense.username ?
              <i className="material-icons">&#xE853;</i> :
              <PlayerIcon click={ () => { return; } } img={ red.defense.img } />
          }
          <span className="name">Tor</span>
        </div>

        <div
          className={ isLastSet ? 'change-positions' : 'hidden' }
          onClick={ (e) => stopEvent(e) && switchPlayerPositions('red') }
        >
          <i className="material-icons">&#xE0C3;</i>
        </div>

        <div className="add-attack" onClick={ (e) => stopEvent(e) && selectPlayer('red', 'attack') }>
          {
            !red.attack.username ?
              <i className="material-icons">&#xE853;</i> :
              <PlayerIcon click={ () => { return; } } img={ red.attack.img } />
          }
          <span className="name">Angriff</span>
        </div>
      </div>

      <div className="setcounter">
        <div>
          <span>{ setNumber }</span>
        </div>
      </div>

      <div className="team-blue" onClick={ () => incGoals('blue') }>
        <div className="goal-counter-container">
          <Swipeable onSwipeLeft={ () => incGoals('blue') } onSwipeRight={ () => decGoals('blue') }>
            <div className="goal-counter">
              <span className="current-goals">{ blue.goals }</span>
            </div>
          </Swipeable>
        </div>

        <div className="add-defense" onClick={ (e) => stopEvent(e) && selectPlayer('blue', 'defense') }>
          {
            !blue.defense.username ?
              <i className="material-icons">&#xE853;</i> :
              <PlayerIcon click={ () => { return; } } img={ blue.defense.img } />
          }
          <span className="name">Tor</span>
        </div>

        <div
          className={ isLastSet ? 'change-positions' : 'hidden' }
          onClick={ (e) => stopEvent(e) && switchPlayerPositions('blue') }
        >
          <i className="material-icons">&#xE0C3;</i>
        </div>

        <div className="add-attack" onClick={ (e) => stopEvent(e) && selectPlayer('blue', 'attack') }>
          {
            !blue.attack.username ?
              <i className="material-icons">&#xE853;</i> :
              <PlayerIcon click={ () => { return; } } img={ blue.attack.img } />
          }
          <span className="name">Angriff</span>
        </div>
      </div>

    </div>
  );
}

export function mapStateToProps({ranked: { selectPlayerFor, teams, sets }}: any) {
  const currentSet = sets[sets.length - 1];

  const getTeamPositions = (team: Team, offense: string) => {
    if (offense === 'player1') {
      return { attack: team.player1, defense: team.player2 };
    } else {
      return { attack: team.player2, defense: team.player1 };
    }
  };

  let winner: TeamColor | null = null;
  if (teams.red.wonSets === POINTS_PER_MATCH) {
    winner = 'red';
  } else if (teams.blue.wonSets === POINTS_PER_MATCH) {
    winner = 'blue';
  }

  return {
    sets,
    teams,
    selectPlayerFor,
    setNumber: sets.length,
    red: { ...getTeamPositions(teams.red, currentSet.offense.red), goals: currentSet.goals.red },
    blue: { ...getTeamPositions(teams.blue, currentSet.offense.blue), goals: currentSet.goals.blue },
    winner
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
  return {
    incGoals: (team: TeamColor) => dispatch(Actions.incGoals(team)),
    decGoals: (team: TeamColor) => dispatch(Actions.decGoals(team)),
    selectPlayer: (team: TeamColor, position: PlayerPostion) => {
      dispatch(Actions.selectPlayer(team, position));
      dispatch(push('/select'));
    },
    setPlayer: (team: TeamColor, position: PlayerKey, player: Player) =>
      dispatch(Actions.setPlayer(team, position, player)),
    switchPlayerPositions: (team: TeamColor) => dispatch(Actions.switchPlayerPositions(team)),
    startNewMatch: () => dispatch(Actions.startNewMatch())
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Match);
