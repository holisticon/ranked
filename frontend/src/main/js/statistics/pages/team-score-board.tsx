import * as React from 'react';
import { ChartData2D, ChartData3D } from '../types';
import { HeadingComponent, HeadingConfig } from '../components/heading';
import { Heading } from '../services/heading.service';
import { Carousel } from 'react-responsive-carousel';
import { RankingChart } from '../components/ranking-chart';
import { TeamStatsAdapter } from '../services/team-stats-adapter';
import { TwoSideBarChart } from '../components/two-side-bar-chart';

type ScoreBoardState = {
  teamGoalRatio: ChartData3D<string, number, number>,
  teamTimeToScore: ChartData2D<string, number>
};


export class TeamScoreBoard extends React.Component<any, ScoreBoardState> {

  private headings: Array<HeadingConfig>;

  constructor(props: any) {
    super(props);
    this.state = { ...this.state };

    // init data
    this.headings = [
      { title: 'Torverhältnis', iconPath: '/img/goal.png' },
      { title: 'Schießt Tore nach durchschnittlich', iconPath: '/img/stopwatch.png' },
    ];

    this.updateList();
  }

  private updateList(): void {
    TeamStatsAdapter.getTeamStatsChartData().then(
      ({teamGoalRatio, teamTimeToScore}) =>
      this.setState({teamGoalRatio, teamTimeToScore})
    );
  }

  private updateHeading(index: number): void {
    Heading.Service.update(this.headings[index]);
  }

  public render() {
    return (
      <div className="score-board">
        <HeadingComponent title={ this.headings[0].title } iconPath={ this.headings[0].iconPath } />
        <Carousel
          swipeScrollTolerance={ 130 }
          onChange={ (index) => this.updateHeading(index) }
          autoPlay={ true }
          showThumbs={ false }
          infiniteLoop={ true }
          interval={ 20000 }
          showStatus={ false }
          showArrows={ false }
        >
          <div className="chart-container">
            <div className="fading-top" />
            <TwoSideBarChart
              data = { !this.state ? undefined : this.state.teamGoalRatio }
              cumulationHeadline = "Torverhältnis"
              cumulate = { (a: number, b: number) => (a === 0 ? 0 : b/a).toFixed(2) }
            />
          </div>
          <div className="chart-container">
            <div className="fading-top" />
            <RankingChart data={ !this.state ? undefined : this.state.teamTimeToScore } />
          </div>
        </Carousel>
      </div>
    );
  }

}
