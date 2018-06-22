import * as React from 'react';
import 'react-responsive-carousel/lib/styles/carousel.min.css';
import './profile.css';
import { PlayerProfileData } from '../types';
import { Player } from '../../types/types';
import { match as Match } from 'react-router';
import { HeadingComponent } from '../components/heading';

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
    // TODO: add backend call to gets the needed data
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
    };
  }

  private calcTime(timeInSec: number): string {
    const seconds = timeInSec % 60;
    const minutes = Math.floor(timeInSec / 60);

    return (minutes > 0 ? minutes + 'm ' : '') + seconds + 's';
  }

  private getMetric (name: string, value: number, unit?: string) {
    let displayValue = value + (unit ? (' ' + unit) : '');

    if (unit === 'time') {
      displayValue = this.calcTime(value);
    }

    return (
      <div className="box">
        <div className="metric">
          <div className="value">{ displayValue }</div>
          <div className="label">{ name }</div>
        </div>
      </div>
    );
  }

  public render() {
    return (
      <div className="profile-page">
        <HeadingComponent
          title={ this.state.playerName }
          iconPath={ this.state.player ? this.state.player.imageUrl : '' }
        />
        <div className="profile">
          <div className="group">
            <div className="heading">Spiele</div>
            { this.getMetric('Aller Spiele gewonnen', this.state.playerProfileData.gameStatistics.wonPercent, '%') }
            { this.getMetric('Spiele gespielt', this.state.playerProfileData.gameStatistics.played) }
            { this.getMetric('Durchschnittliche Spielzeit',
                             this.state.playerProfileData.gameStatistics.avgTime, 'time') }
          </div>
          <div className="group">
            <div className="heading">Sätze</div>
            { this.getMetric('Aller Sätze gewonnen', this.state.playerProfileData.setStatistics.wonPercent, '%') }
            { this.getMetric('Sätze gespielt', this.state.playerProfileData.setStatistics.played) }
            { this.getMetric('Durchschnittliche Satzzeit',
                             this.state.playerProfileData.setStatistics.avgTime, 'time') }
          </div>
          <div className="group">
            <div className="heading">Tore</div>
            { this.getMetric('Tore geschossen', this.state.playerProfileData.goalStatistics.scored) }
            { this.getMetric('Torverhältnis', this.state.playerProfileData.goalStatistics.ratio) }
            { this.getMetric('Durchschnittliche Zeit zum Tor',
                             this.state.playerProfileData.goalStatistics.avgTimeToScore, 'time') }
          </div>
        </div>
      </div>
    );
  }
}
