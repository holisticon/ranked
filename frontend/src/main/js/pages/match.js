import React from 'react';
import { Swipeable } from 'react-touch';

export class Match extends React.Component {
  constructor(props) {
    super(props);

    this.initState();
  }

  initState() {
    this.state = {
      teams: {
        blue: { goals: 0, player1: '', player2: '' },
        red: { goals: 0, player1: '', player2: '' }
      }
    };
  }

  changeGoals(team, diff) {
    this.state.teams[team].goals += diff;
    this.setState(this.state);
  }

  incGoals(team) {
    this.changeGoals(team, 1);
  }

  decGoals(team) {
    this.changeGoals(team, -1);
  }

  switchPlayerPositions(teamColor) {
    const team = this.state.teams[teamColor];
    [team.player1, team.player2] = [team.player2, team.player1];

    this.setState(this.state);
  }

  render() {
    return (
      <div className="match">

        <div className="team-red">
          <div className="add-defense">
            <i className="material-icons">&#xE853;</i>
            <span className="name">Tor</span>
          </div>

          <div className="add-attack">
            <i className="material-icons">&#xE853;</i>
            <span className="name">Angriff</span>
          </div>

          <Swipeable onSwipeRight={ () => this.incGoals('red') } onSwipeLeft={ () => this.decGoals('red') }>
            <div className="goal-counter" onClick={ () => this.incGoals('red') }>
              <span className="current-goals">{ this.state.teams.red.goals }</span>
            </div>
          </Swipeable>
        </div>

        <div className="team-blue">
          <div className="add-defense">
            <i className="material-icons">&#xE853;</i>
            <span className="name">Tor</span>
          </div>

          <div className="add-attack">
            <i className="material-icons">&#xE853;</i>
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