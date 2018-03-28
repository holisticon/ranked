import * as React from 'react';
import { Carousel } from 'react-responsive-carousel';
import 'react-responsive-carousel/lib/styles/carousel.min.css';
import { Player } from '../../types/types';
import { RankingChart } from '../components/ranking-chart';
import { ChartData2D } from '../types';
import { EloAdapter } from '../adapter/elo-adapter';
import { GoalsAdapter } from '../adapter/goals-adapter';
import './score-board.css';

type ScoreBoardState = {
  playerValues: ChartData2D<Player, number>,
  playerGoals: ChartData2D<Player, number>,
  headings: Array<{ title: string, icon: string }>,
  currentCarouselIndex: number
};

export class ScoreBoard extends React.Component<any, ScoreBoardState> {
  constructor(props: any) {
    super(props);
    this.state = { ...this.state, currentCarouselIndex: 0, headings: [] };

    // init data
    this.updateList();
    setInterval(() => this.updateList(), 60 * 1000);
  }

  private updateList(): void {
    Promise.all([EloAdapter.getEloData(), GoalsAdapter.getTotalGoalsData()])
      .then(([eloData, totalGoalsData]) => {
        const headings = [
          { title: 'Holisticon AllStars', icon: '/img/trophy.png' },
          { title: 'Erzielte Tore', icon: '/img/goal.png' }
        ];
        this.setState({ playerValues: eloData, playerGoals: totalGoalsData, headings });
      });
  }

  private getHeaderIconPath(): string {
    if (this.state.currentCarouselIndex >= this.state.headings.length) {
      return '';
    }

    return this.state.headings[this.state.currentCarouselIndex].icon;
  }

  private getTitle(): string {
    if (this.state.currentCarouselIndex >= this.state.headings.length) {
      return '';
    }

    return this.state.headings[this.state.currentCarouselIndex].title;
  }

  public render() {
    return (
      <div className="score-board">
        <div className="header">
          <div className="background" />
          <div className="icon" style={{ backgroundImage: `url(${this.getHeaderIconPath()})` }} />
          <div className="title">{this.getTitle()}</div>
        </div>
        <Carousel swipeScrollTolerance={130} onChange={ (index) => this.setState({ currentCarouselIndex: index }) } autoPlay={true} showThumbs={false} infiniteLoop={true} interval={10000} showStatus={false} showArrows={false}>
          <div className="ranking-list">
            <div className="fading-top" />
            <RankingChart data={ !this.state ? undefined : this.state.playerValues } />
          </div>
          <div className="ranking-list">
            <div className="fading-top" />
            <RankingChart data={ !this.state ? undefined : this.state.playerGoals } />
          </div>
        </Carousel>
      </div>
    );
  }
}
