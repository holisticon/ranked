import './score-board.css';
import 'react-responsive-carousel/lib/styles/carousel.min.css';

import * as qs from 'query-string';
import * as React from 'react';
import { Carousel } from 'react-responsive-carousel';

import { Player } from '../../types/types';
import { HeadingComponent, HeadingConfig } from '../components/heading';
import { RankingChart } from '../components/ranking-chart';
import { EloAdapter } from '../services/elo-adapter';
import { GoalsAdapter } from '../services/goals-adapter';
import { Heading } from '../services/heading.service';
import { ChartData2D, ChartData3D } from '../types';
import { ProfileSelection } from './profile-selection';

type ScoreBoardState = {
  playerEloData: ChartData2D<Player, number>,
  playerGoalRatio: ChartData2D<Player, string>,
  playerConcededScoredGoalsData: ChartData3D<Player, number, number>,
  playerPositionGoalsData: ChartData3D<Player, number, number>,
  playerTimeToScore: ChartData2D<Player, number>,
};

type Container = {
  type: string,
  data: any
};

export class ScoreBoard extends React.Component<any, ScoreBoardState> {
  private isInteractive: boolean = false;
  private headings: Array<HeadingConfig>;

  constructor(props: any) {
    super(props);
    this.state = { ...this.state };

    if (this.props.location && this.props.location.search) {
      const interactiveParam = qs.parse(this.props.location.search).interactive;
      if (interactiveParam && typeof interactiveParam === 'string') {
        this.isInteractive = 'true' === interactiveParam.toLowerCase();
      }
    }

    // init data
    const interactivePages = this.isInteractive ? [
      { title: 'Steckbrief', iconPath: '/img/profile.png' }
    ] : [];

    this.headings = [
      ...interactivePages,
      { title: 'Holisticon AllStars', iconPath: '/img/trophy.png' },
      { title: 'Torverhältnis', iconPath: '/img/goal.png' },
      { title: 'Schießt Tor nach', iconPath: '/img/stopwatch.png' }
    ];
    this.updateList();
    setInterval(() => this.updateList(), 80 * 1000);
  }

  private updateList(): void {
    Promise
      .all([
        EloAdapter.getEloData(),
        GoalsAdapter.getTotalGoalsData(),
        GoalsAdapter.getPlayerAvgScoreTimeData()
      ])
      .then(([
        playerEloData,
        playerGoalRatio,
        playerTimeToScore
      ]) => {
        this.setState({
          playerEloData,
          playerGoalRatio,
          playerTimeToScore
        });
      });
  }

  private updateHeading(index: number): void {
    Heading.Service.update(this.headings[index]);
  }

  private renderContainers(containers: Array<Container>): any {
    return containers.map((con, index) => {
      return (
        <div key={ index } className="container">
          <div className="fading-top" />
          <div className="container-inner">
            { con.type === 'chart' ? <RankingChart data={con.data} /> : null }
            { con.type === 'profile' ? <ProfileSelection embedded={true} /> : null }
          </div>
          <div className="fading-bottom" />
        </div>
      );
    });
  }

  public render() {
    const rankings: Array<Container> = !this.state ? [] : [
      { type: 'chart', data: this.state.playerEloData },
      { type: 'chart', data: this.state.playerGoalRatio },
      { type: 'chart', data: this.state.playerTimeToScore }
    ];

    const interactivePages: Array<Container> = !this.isInteractive ? [] : [
      { type: 'profile', data: null }
    ];

    return (
      <div className={ 'score-board' + (this.isInteractive ? ' interactive' : ' display' ) }>
        <HeadingComponent title={this.headings[0].title} iconPath={this.headings[0].iconPath} />
        <Carousel
          swipeScrollTolerance={130}
          onChange={(index) => this.updateHeading(index)}
          autoPlay={!this.isInteractive}
          showThumbs={false}
          infiniteLoop={true}
          interval={20000}
          showStatus={false}
          showArrows={false}
        >
          { this.renderContainers([...interactivePages, ...rankings]) }
        </Carousel>
      </div>
    );
  }
}
