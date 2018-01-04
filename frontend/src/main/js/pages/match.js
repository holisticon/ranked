import React from 'react';
import { Swipeable } from 'react-touch';
import { SelectPlayer } from '../components/select_player';
import { PlayerIcon } from '../components/player_icon';

export class Match extends React.Component {
  constructor(props) {
    super(props);

    this.initState();
  }

  initState() {
    this.state = {
      selectPlayerFor: null,
      teams: {
        blue: { goals: 0, attack: null, defense: null },
        red: { goals: 0, attack: null, defense: null }
      }
    };
  }

  changeGoals(team, diff) {
    this.state.teams[team].goals += diff;
    this.forceUpdate();
  }

  incGoals(team) {
    this.changeGoals(team, 1);
  }

  decGoals(team) {
    this.changeGoals(team, -1);
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

  playerSelected(player) {
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