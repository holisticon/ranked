import * as React from 'react';
import { Swipeable } from 'react-touch';
import { SelectPlayer } from '../components/select_player';
import { PlayerIcon } from '../components/player_icon';
import { TeamColor, PlayerPostion, Player, Team, PlayerKey } from '../types/types';
import { connect, Dispatch } from 'react-redux';
import { StoreState } from '../types/store.state';
import * as Actions from '../actions';
import { POINTS_PER_MATCH } from '../config';
import { Dialog } from '../components/dialog';
import './match.css';

interface ActiveTeam {
  goals: number;
  attack: Player | null;
  defense: Player | null;
}

export interface MatchProps {
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

function endMatch() {
  // TODO: send match data to backend
}

function Match({ selectPlayerFor, setNumber, red, blue, incGoals, winner,
  decGoals, selectPlayer, setPlayer, switchPlayerPositions, startNewMatch }: MatchProps) {
  
  const isLastSet = setNumber === (POINTS_PER_MATCH * 2 - 1);

  return (
    <div className="match">
      {
        winner &&
        <Dialog 
          headline="Spiel beendet"
          text="Ente Ente Ente Ente Ente Ente Ente Ente Ente"
          buttons={ [{ text: 'ok', type: 'ok', click: () => {
            endMatch();
            startNewMatch();
          } }] }
        />
      }

      <SelectPlayer 
        visible={ !!selectPlayerFor }
        upsideDown={ !!selectPlayerFor && selectPlayerFor.team === 'red' }
        select={ (player: Player) => setPlayer(selectPlayerFor.team, selectPlayerFor.position, player) }
      />

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
            !red.defense ?
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
            !red.attack ?
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
            !blue.defense ?
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
            !blue.attack ?
              <i className="material-icons">&#xE853;</i> :
              <PlayerIcon click={ () => { return; } } img={ blue.attack.img } />
          }
          <span className="name">Angriff</span>
        </div>
      </div>

    </div>
  );
}

/* 
export class Match extends React.Component {

  addSet(blueTeam, redTeam) {
    this.state.sets.push({
      number: this.state.sets.length + 1,
      blue: Object.assign({}, blueTeam, { goals : 0}),
      red: Object.assign({}, redTeam, { goals : 0})
    });
  }

  endSet(winnerTeam) {
    if (++this.currentSet[winnerTeam].won >= POINTS_PER_MATCH) {
      this.endMatch(winnerTeam);
      return;
    }

    // match is not ended, so start a new set with switched teams
    this.addSet(this.currentSet.red, this.currentSet.blue);

    // switch player positions per team
    // TODO: calculate "best" positions for last turn?
    this.switchPlayerPositions('red');
    this.switchPlayerPositions('blue');

    // reset goals
    this.currentSet.red.goals = 0;
    this.currentSet.blue.goals = 0;
  }

  endMatch(winnerTeam) {
    // TODO

    const team = this.currentSet[winnerTeam];
    setTimeout(() => {
      alert(`${team.attack.name} und ${team.defense.name} haben gewonnen!`);
    }, 100);

    this.initState();
  }

  switchPlayerPositions(teamColor) {
    const team = this.currentSet[teamColor];
    [team.attack, team.defense] = [team.defense, team.attack];
    this.forceUpdate();
  }

  selectPlayer(team, position, event) {
    event.preventDefault();
    event.stopPropagation();

    this.state.selectPlayerFor = { team, position };
    this.forceUpdate();
  }

  removePlayer(player) {
    if (this.currentSet.blue.attack === player) {
      this.currentSet.blue.attack = null;
    }
    
    if (this.currentSet.blue.defense === player) {
      this.currentSet.blue.defense = null;
    }

    if (this.currentSet.red.attack === player) {
      this.currentSet.red.attack = null;
    }

    if (this.currentSet.red.defense === player) {
      this.currentSet.red.defense = null;
    }
  }

  playerSelected(player) {
    this.removePlayer(player);
    this.currentSet[this.state.selectPlayerFor.team][this.state.selectPlayerFor.position] = player;
    this.state.selectPlayerFor = null;
    this.forceUpdate();
  }

}
 */

export function mapStateToProps({ selectPlayerFor, teams, sets }: StoreState) {
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
    selectPlayer: (team: TeamColor, position: PlayerPostion) => dispatch(Actions.selectPlayer(team, position)),
    setPlayer: (team: TeamColor, position: PlayerKey, player: Player) =>
      dispatch(Actions.setPlayer(team, position, player)),
    switchPlayerPositions: (team: TeamColor) => dispatch(Actions.switchPlayerPositions(team)),
    startNewMatch: () => dispatch(Actions.startNewMatch())
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Match);
