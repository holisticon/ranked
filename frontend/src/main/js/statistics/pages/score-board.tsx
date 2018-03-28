import * as React from 'react';
import './score-board.css';
import { Player } from '../../types/types';
import { RankingChart } from '../components/ranking-chart';
import { ChartData2D } from '../types';
import { EloAdapter } from '../adapter/elo-adapter';

type ScoreBoardState = {
  playerValues: ChartData2D<Player, number>,
};

export class ScoreBoard extends React.Component<any, ScoreBoardState> {
  constructor(props: any) {
    super(props);

    // init data
    this.updateList();
    setInterval(() => this.updateList(), 60 * 1000);
  }

  private updateList(): void {
    EloAdapter.getEloData().then(playerElos => {
      this.setState({ playerValues: playerElos });
    });
  }

  private getHeaderIconPath(): string {
    return '/img/trophy.png';
  }

  private getTitle(): string {
    return 'Holisticon AllStars';
  }

  public render() {
    return (
      <div className="score-board">
        <div className="header">
          <div className="background" />
          <div className="icon" style={{ backgroundImage: `url(${this.getHeaderIconPath()})` }} />
          <div className="title">{this.getTitle()}</div>
        </div>
        <div className="ranking-list">
          <div className="fading-top" />
          <RankingChart data={ !this.state ? undefined : this.state.playerValues } />
        </div>
      </div>
    );
  }
}