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
      sets: [],
    };

    const newTeam = { won: 0, attack: null, defense: null };
    this.addSet(newTeam, newTeam);
  }

  get currentSet() {
    return this.state.sets[this.state.sets.length - 1];
  }

  changeGoals(team, diff) {
    this.currentSet[team].goals += diff;

    if (this.currentSet[team].goals < 0) {
      this.currentSet[team].goals = 0;
    } else if (this.currentSet[team].goals >= POINTS_PER_SET) {
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
  }

  selectPlayer(team, position) {
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

  isLastSet() {
    return this.currentSet.number == POINTS_PER_MATCH * 2 - 1;
  }

  render() {
    return (
      <div className="match">
        <SelectPlayer visible={ !!this.state.selectPlayerFor } select={ (player) => this.playerSelected(player) } ></SelectPlayer>

        <div className="team-red">
          <div className="add-defense" onClick={ () => this.selectPlayer('red', 'defense') }>
            {
              !this.currentSet.red.defense ?
                <i className="material-icons">&#xE853;</i> :
                <PlayerIcon name={ this.currentSet.red.defense.name } img={ this.currentSet.red.defense.img }></PlayerIcon>
            }
            <span className="name">Tor</span>
          </div>

          <div className={ this.isLastSet() ? 'change-positions' : 'hidden' }>
            <i className="material-icons">&#xE0C3;</i>
          </div>

          <div className="add-attack" onClick={ () => this.selectPlayer('red', 'attack') }>
            {
              !this.currentSet.red.attack ?
                <i className="material-icons">&#xE853;</i> :
                <PlayerIcon name={ this.currentSet.red.attack.name } img={ this.currentSet.red.attack.img }></PlayerIcon>
            }
            <span className="name">Angriff</span>
          </div>

          <Swipeable onSwipeRight={ () => this.incGoals('red') } onSwipeLeft={ () => this.decGoals('red') }>
            <div className="goal-counter" onClick={ () => this.incGoals('red') }>
              <span className="current-goals">{ this.currentSet.red.goals }</span>
            </div>
          </Swipeable>
        </div>
        
        <div className="setcounter">
          <div>
            <span>{ this.currentSet.number }</span>
          </div>
        </div>

        <div className="team-blue">
          <div className="add-defense" onClick={ () => this.selectPlayer('blue', 'defense') }>
            {
              !this.currentSet.blue.defense ?
                <i className="material-icons">&#xE853;</i> :
                <PlayerIcon name={ this.currentSet.blue.defense.name } img={ this.currentSet.blue.defense.img }></PlayerIcon>
            }
            <span className="name">Tor</span>
          </div>

          <div className={ this.isLastSet() ? 'change-positions' : 'hidden' }>
            <i className="material-icons">&#xE0C3;</i>
          </div>

          <div className="add-attack" onClick={ () => this.selectPlayer('blue', 'attack') }>
            {
              !this.currentSet.blue.attack ?
                <i className="material-icons">&#xE853;</i> :
                <PlayerIcon name={ this.currentSet.blue.attack.name } img={ this.currentSet.blue.attack.img }></PlayerIcon>
            }
            <span className="name">Angriff</span>
          </div>

          <Swipeable onSwipeLeft={ () => this.incGoals('blue') } onSwipeRight={ () => this.decGoals('blue') }>
            <div className="goal-counter" onClick={ () => this.incGoals('blue') }>
              <span className="current-goals">{ this.currentSet.blue.goals }</span>
            </div>
          </Swipeable>
        </div>

      </div>
    );
  }
}
