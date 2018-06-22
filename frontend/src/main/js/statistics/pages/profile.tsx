import * as React from 'react';
import 'react-responsive-carousel/lib/styles/carousel.min.css';
import './score-board.css';
import { PlayerProfileData } from '../types';
import { Player } from '../../types/types';
import { match as Match } from 'react-router';


type ProfileState = {
  player: Player;
  playerName: string;
  playerProfileData: PlayerProfileData;
};

export class Profile extends React.Component<any, ProfileState> {

  constructor(props: any, match: Match<any>) {
    super(props);

    let playerName = '';

    if (match && match.params) {
      playerName = match.params.playerName;
    }

    this.state = { playerName, playerProfileData: this.getPlayerProfileData() } as ProfileState;
  }

  private getPlayerProfileData(): PlayerProfileData {
    return {
      gameStatistics: {
        wonPercent: 63.3,
        played: 108,
        avgTime: 513
      },
      setStatistics: {
        wonPercent: 59,
        played: 221,
        avgTime: 197,
      },
      goalStatistics: {
        scored: 678,
        ratio: 2.64,
        avgTimeToScore: 22.4
      },
      eloData: {
        dimensions: [{description: 'Datum'}, {description: 'Elo'}],
        entries: [
          [new Date('2018-06-04T11:45:14'), 1000],
          [new Date('2018-06-10T14:15:24'), 1005],
          [new Date('2018-06-12T09:34:11'), 1008],
          [new Date('2018-06-16T16:17:01'), 1011],
        ]
      }
    }
  }

  private getMetric (name: string, value: number, unit?: string) {
    return (
      <div className="metric">
        <div className="value">{ value }{ unit }</div>
        <div className="label">{ name }</div>
      </div>
    )
  }

  public render() {
    return (
      <div className="profile">
        <div className="group">
          <div className="heading">Spiele</div>
          { this.getMetric('Spiele gewonnen', this.state.playerProfileData.gameStatistics.wonPercent, '%') }
          { this.getMetric('Spiele gespielt', this.state.playerProfileData.gameStatistics.played) }
          { this.getMetric('Durchschnittliche Spielzeit', this.state.playerProfileData.gameStatistics.avgTime, 's') }
        </div>
      </div>
    );
  }
}
