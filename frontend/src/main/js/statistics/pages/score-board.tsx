import * as React from 'react';
import { Carousel } from 'react-responsive-carousel';
import 'react-responsive-carousel/lib/styles/carousel.min.css';
import { Player } from '../../types/types';
import { RankingChart } from '../components/ranking-chart';
import { ChartData2D } from '../types';
import { EloAdapter } from '../services/elo-adapter';
import { GoalsAdapter } from '../services/goals-adapter';
import './score-board.css';
import { Heading } from '../services/heading.service';
import { HeadingComponent, HeadingConfig } from '../components/heading';

type ScoreBoardState = {
  playerValues: ChartData2D<Player, number>,
  playerGoals: ChartData2D<Player, number>,
};

export class ScoreBoard extends React.Component<any, ScoreBoardState> {
  private headings: Array<HeadingConfig>;

  constructor(props: any) {
    super(props);
    this.state = { ...this.state };

    // init data
    this.headings = [
      { title: 'Holisticon AllStars', iconPath: '/img/trophy.png' },
      { title: 'Erzielte Tore', iconPath: '/img/goal.png' }
    ];
    this.updateList();
    setInterval(() => this.updateList(), 60 * 1000);
  }

  private updateList(): void {
    Promise.all([EloAdapter.getEloData(), GoalsAdapter.getTotalGoalsData()])
      .then(([eloData, totalGoalsData]) => {
        this.setState({ playerValues: eloData, playerGoals: totalGoalsData });
      });
  }

  private updateHeading(index: number): void {
    Heading.Service.update(this.headings[index]);
  }

  public render() {
    return (
      <div className="score-board">
        <HeadingComponent title={ this.headings[0].title } iconPath={ this.headings[0].iconPath } />
        <Carousel swipeScrollTolerance={130} onChange={ (index) => this.updateHeading(index) } autoPlay={true} showThumbs={false} infiniteLoop={true} interval={10000} showStatus={false} showArrows={false}>
          <div className="chart-container">
            <div className="fading-top" />
            <RankingChart data={ !this.state ? undefined : this.state.playerValues } />
          </div>
          <div className="chart-container">
            <div className="fading-top" />
            <RankingChart data={ !this.state ? undefined : this.state.playerGoals } />
          </div>
        </Carousel>
      </div>
    );
  }
}
