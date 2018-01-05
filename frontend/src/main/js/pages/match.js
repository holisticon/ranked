import React from 'react';
import { Swipeable } from 'react-touch';
import { SelectPlayer } from '../components/select_player';
import { PlayerIcon } from '../components/player_icon';

const POINTS_PER_SET = 6;
const POINTS_PER_MATCH = 2;

export class Match extends React.Component {
  constructor(props) {
    super(props);

    this.initState();
  }

  initState() {
    this.state = {
      selectPlayerFor: null,
      teams: {
        blue: { goals: 0, won: 0, attack: null, defense: null },
        red: { goals: 0, won: 0, attack: null, defense: null }
      }
    };
  }

  changeGoals(team, diff) {
    this.state.teams[team].goals += diff;
    
    if (this.state.teams[team].goals < 0) {
      this.state.teams[team].goals = 0;
    } else if (this.state.teams[team].goals >= POINTS_PER_SET) {
      this.endSet(team);
    }

    this.forceUpdate();
  }

  incGoals(team) {
    this.changeGoals(team, 1);
  }

  decGoals(team) {
    this.changeGoals(team, -1);
  }

  endSet(winnerTeam) {
    if (++this.state.teams[winnerTeam].won >= POINTS_PER_MATCH) {
      this.endMatch(winnerTeam);
      return;
    }
    
    // switch teams
    [this.state.teams.red, this.state.teams.blue] = [this.state.teams.blue, this.state.teams.red];

    // switch player positions per team
    // TODO: calculate "best" positions for last turn?`
    this.switchPlayerPositions('red');
    this.switchPlayerPositions('blue');

    // reset goals
    this.state.teams.red.goals = 0;
    this.state.teams.blue.goals = 0;
  }

  endMatch(winnerTeam) {
    // TODO

    const team = this.state.teams[winnerTeam];
    setTimeout(() => {
      alert(`${team.attack.name} und ${team.defense.name} haben gewonnen!`);
    }, 100);

    this.initState();
  }

  switchPlayerPositions(teamColor) {
    const team = this.state.teams[teamColor];
    [team.attack, team.defense] = [team.defense, team.attack];

    this.forceUpdate();
  }

  selectPlayer(team, position) {
    this.state.selectPlayerFor = { team, position };
    this.forceUpdate();
  }

  removePlayer(player) {
    if (this.state.teams.blue.attack === player) {
      this.state.teams.blue.attack = null;
    }
    
    if (this.state.teams.blue.defense === player) {
      this.state.teams.blue.defense = null;
    }

    if (this.state.teams.red.attack === player) {
      this.state.teams.red.attack = null;
    }

    if (this.state.teams.red.defense === player) {
      this.state.teams.red.defense = null;
    }
  }

  playerSelected(player) {
    this.removePlayer(player);
    this.state.teams[this.state.selectPlayerFor.team][this.state.selectPlayerFor.position] = player;
    this.state.selectPlayerFor = null;
    this.forceUpdate();
  }

  render() {
    return (
      <div className="match">
        <SelectPlayer visible={ !!this.state.selectPlayerFor } select={ (player) => this.playerSelected(player) } ></SelectPlayer>

        <div className="team-red">
          <div className="add-defense" onClick={ () => this.selectPlayer('red', 'defense') }>
            {
              !this.state.teams.red.defense ?
                <i className="material-icons">&#xE853;</i> :
                <PlayerIcon name={ this.state.teams.red.defense.name } img={ this.state.teams.red.defense.img }></PlayerIcon>
            }
            <span className="name">Tor</span>
          </div>

          <div className="add-attack" onClick={ () => this.selectPlayer('red', 'attack') }>
            {
              !this.state.teams.red.attack ?
                <i className="material-icons">&#xE853;</i> :
                <PlayerIcon name={ this.state.teams.red.attack.name } img={ this.state.teams.red.attack.img }></PlayerIcon>
            }
            <span className="name">Angriff</span>
          </div>

          <Swipeable onSwipeRight={ () => this.incGoals('red') } onSwipeLeft={ () => this.decGoals('red') }>
            <div className="goal-counter" onClick={ () => this.incGoals('red') }>
              <span className="current-goals">{ this.state.teams.red.goals }</span>
            </div>
          </Swipeable>
        </div>

        <div className="team-blue">
          <div className="add-defense" onClick={ () => this.selectPlayer('blue', 'defense') }>
            {
              !this.state.teams.blue.defense ?
                <i className="material-icons">&#xE853;</i> :
                <PlayerIcon name={ this.state.teams.blue.defense.name } img={ this.state.teams.blue.defense.img }></PlayerIcon>
            }
            <span className="name">Tor</span>
          </div>

          <div className="add-attack" onClick={ () => this.selectPlayer('blue', 'attack') }>
            {
              !this.state.teams.blue.attack ?
                <i className="material-icons">&#xE853;</i> :
                <PlayerIcon name={ this.state.teams.blue.attack.name } img={ this.state.teams.blue.attack.img }></PlayerIcon>
            }
            <span className="name">Angriff</span>
          </div>

          <Swipeable onSwipeLeft={ () => this.incGoals('blue') } onSwipeRight={ () => this.decGoals('blue') }>
            <div className="goal-counter" onClick={ () => this.incGoals('blue') }>
              <span className="current-goals">{ this.state.teams.blue.goals }</span>
            </div>
          </Swipeable>
        </div>

      </div>
    );
  }
}