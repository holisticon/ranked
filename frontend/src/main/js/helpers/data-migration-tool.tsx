import axios from 'axios';
import * as React from 'react';

import { PlayerKey } from '../types/types';

const data = require('./ranked-interims-event-data.json');

type DataMigrationToolState = {
  logs: Array<string>
};

type PlayedSet = {
  type: 'timestamp',
  goals: Array<{ first: string, second: Date }>,
  offenseRed: { value: string },
  offenseBlue: { value: string }
};

type Match = {
  teamRed: {
    player1: { value: string },
    player2: { value: string }
  },
  teamBlue: {
    player1: { value: string },
    player2: { value: string }
  },
  matchSets: Array<PlayedSet>,
  startTime: Date
};

export class DataMigrationTool extends React.Component<any, DataMigrationToolState> {
  constructor(props: any) {
    super(props);
    this.state = { logs: [] };
  }

  private newMatch(): Match {
    return {
      teamRed: { player1: { value: '' }, player2: { value: '' } },
      teamBlue: { player1: { value: '' }, player2: { value: '' } },
      matchSets: [],
      startTime: new Date()
    };
  }

  private newSet(): PlayedSet {
    return { type: 'timestamp', goals: [], offenseBlue: { value: '' }, offenseRed: { value: '' } };
  }

  private otherPlayer(player: PlayerKey): PlayerKey {
    return player === 'player1' ? 'player2' : 'player1';
  }

  private otherTeam(team: string): string {
    return team.toUpperCase() === 'RED' ? 'BLUE' : 'RED';
  }

  private isValid(match: Match): boolean {
    if (!match.teamBlue.player1.value || !match.teamBlue.player2.value || !match.teamRed.player1.value || !match.teamRed.player2.value) {
      console.log(`Invalid - players missing! ${ match }`);
      return false;
    }

    if (match.matchSets.length !== 2 && match.matchSets.length !== 3) {
      console.log(`Invalid - number of sets wrong! ${ match }`);
      return false;
    }

    let goalsRed: number;
    let goalsBlue: number;
    let invalidSets: number[] = [];
    match.matchSets.forEach((set, index) => {
      goalsRed = goalsBlue = 0;
      set.goals.forEach(goal => goal.first === 'RED' ? goalsRed++ : goalsBlue++);
      if (goalsRed !== 6 && goalsBlue !== 6) {
        invalidSets.push(index);
      }
    });

    if (invalidSets.length > 0) {
      console.log(`Invalid - set(s) #${ invalidSets.join(', ') } have no winner! ${ match }`);
      return false;
    }

    return true;
  }

  private extractMatches(): Array<Match> {
    const matches: Array<Match> = [];
    let set: PlayedSet = this.newSet();
    let goalsRed = 0;
    let goalsBlue = 0;
    let swapped = false;
    let match: Match = this.newMatch();
    let team;

    let offenseRedKey: PlayerKey = 'player1';
    let offenseBlueKey: PlayerKey = 'player1';

    data.forEach((event: any) => {
      switch (event.type) {
        case 'SET_PLAYER':
          if (set.goals.length > 0) {
            if (goalsRed >= 6 || goalsBlue >= 6) {
              // new match startet without START_NEW_MATCH event
              matches.push(match);
            }
            // reset set container
            set = this.newSet();
            goalsRed = 0;
            goalsBlue = 0;
            offenseRedKey = 'player1';
            offenseBlueKey = 'player1';
            swapped = false;
            // reset match
            match = this.newMatch();
          } else {
            team = (event.team === 'team1') ? match.teamRed : match.teamBlue;
            team[event.player] = { value: event.selected.userName.value };
          }
          break;
        case 'SWITCH_PLAYER_POSITION':
          if (event.team === 'red') {
            offenseRedKey = this.otherPlayer(offenseRedKey);
          } else {
            offenseBlueKey = this.otherPlayer(offenseBlueKey);
          }
          break;
        case 'INC_GOALS':
          if (match.matchSets.length === 0 && set.goals.length === 0) {
            // first goal
            match.startTime = new Date(new Date(event.eventTimestamp).getTime() - (event.time * 1000));
          }
          if (!set.offenseRed.value) {
            set.offenseRed = match.teamRed[offenseRedKey];
            set.offenseBlue = match.teamBlue[offenseBlueKey];
          }
          set.goals.push({ first: swapped ? this.otherTeam(event.team) : event.team.toUpperCase(), second: new Date(event.eventTimestamp) });

          if (event.team === 'red') {
            goalsRed++;
          } else {
            goalsBlue++;
          }

          // set finished?
          if (goalsRed >= 6 || goalsBlue >= 6) {
            // add set
            match.matchSets.push(set);

            // reset set container
            set = this.newSet();
            goalsRed = 0;
            goalsBlue = 0;

            // change offense players
            offenseRedKey = this.otherPlayer(offenseRedKey);
            offenseBlueKey = this.otherPlayer(offenseBlueKey);

            swapped = !swapped;
          }
          break;
        case 'START_NEW_MATCH':
          if (match.matchSets.length > 0) {
            matches.push(match);
          }
          // reset set container
          set = this.newSet();
          goalsRed = 0;
          goalsBlue = 0;
          offenseRedKey = 'player1';
          offenseBlueKey = 'player1';
          swapped = false;
          // reset match
          match = this.newMatch();
          break;
        default:
          break;
      }
    });

    return matches
      .map(m => {
        if (!m.teamBlue.player1.value) {
          m.teamBlue.player1.value = 'gast';
        }
        if (!m.teamBlue.player2.value) {
          m.teamBlue.player2.value = 'gast';
        }
        if (!m.teamRed.player1.value) {
          m.teamRed.player1.value = 'gast';
        }
        if (!m.teamRed.player2.value) {
          m.teamRed.player2.value = 'gast';
        }
        return m;
      })
      .filter(m => this.isValid(m));
  }

  private send(matches: Array<Match>): void {
    let i = 0;
    const interval = setInterval(
      () => {
        if (i >= matches.length) {
          clearInterval(interval);
          return;
        }
        axios.post('command/match', matches[i]).then(
          () => this.setState({ logs: [...this.state.logs, `Match #${ i } erfolgreich migriert`] }),
          () => this.setState({ logs: [...this.state.logs, `Migration von Match #${ i } fehlgeschlagen`] })
        );
        i++;
      },
      500
    );
  }

  public render() {
    return (
      <div>
        { this.state.logs.map((log, i) => {
          return (
            <div key={ i }>{ log }</div>
          );
        }) }
        <input type="button" value="Mach!" onClick={ () => this.send(this.extractMatches()) } />
      </div>
    );
  }
}
