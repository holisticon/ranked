import * as React from 'react';
import 'react-responsive-carousel/lib/styles/carousel.min.css';
import './profile.css';
import { PlayerProfileData } from '../types';
import { Player } from '../../types/types';
import { match as Match } from 'react-router';
import { HeadingComponent } from '../components/heading';
import { PlayerService } from '../../services/player-service';
import { TrendChart } from '../components/trend-chart';
import { MatchAdapter } from '../services/match-adapter';
import { SetAdapter } from '../services/set-adapter';
import { EloAdapter } from '../services/elo-adapter';
import { GoalsAdapter } from '../services/goals-adapter';

type ProfileState = {
  player: Player;
  playerProfileData: PlayerProfileData;
};

export class Profile extends React.Component<any, ProfileState> {

  constructor(props: any, match: Match<any>) {
    super(props);

    let playerId = 'oliverniebsch';

    if (match && match.params) {
      playerId = match.params.playerName;
    }

    this.state = {} as ProfileState;

    PlayerService.getPlayer(playerId).then(player => this.setState({ player }));
    this.getPlayerProfileData(playerId).then(playerProfileData => this.setState({ playerProfileData }));
  }

  private getPlayerProfileData(playerId: string): Promise<PlayerProfileData> {
    // TODO: add backend call to gets the needed data
    return Promise.all([
      MatchAdapter.getMatchStatsForPlayer(playerId),
      SetAdapter.getSetStatsForPlayer(playerId),
      GoalsAdapter.getGoalStatsForPlayer(playerId),
      EloAdapter.getEloHistoryForPlayer(playerId)
    ]).then(([matchStats, setStats, goalStats, eloHistory]) => {
      // tslint:disable-next-line:no-console
      console.log('Got stats!');

      return {
        gameStatistics: matchStats,
        setStatistics: setStats,
        goalStatistics: goalStats,
        eloData: eloHistory
      };
    });
  }

  private calcTime(timeInSec: number): string {
    const seconds = timeInSec % 60;
    const minutes = Math.floor(timeInSec / 60);

    return (minutes > 0 ? minutes + 'm ' : '') + seconds + 's';
  }

  private getMetric (name: string, value: number, unit?: string) {
    let displayValue = (Number.isInteger(value) ? value : value.toFixed(2)) + (unit ? (' ' + unit) : '');

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

  private getPlayersFirstName(): string {
    return this.state.player.displayName.split(' ')[0];
  }

  public render() {
    if (!this.state.player || !this.state.playerProfileData) {
      return null;
    }

    return (
      <div className="profile-page">
        <HeadingComponent
          title={ this.getPlayersFirstName() }
          iconPath={ this.state.player.imageUrl }
          showBackButton= { true }
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
          <div className="group">
            <div className="heading">Elo-Trend</div>
            <div className="chart">
              <TrendChart data={ this.state.playerProfileData.eloData } />
            </div>
          </div>
        </div>
      </div>
    );
  }
}
