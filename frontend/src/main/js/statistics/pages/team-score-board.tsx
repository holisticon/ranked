import * as React from 'react';
import { ChartData2D, ChartData3D } from '../types';
import { HeadingComponent, HeadingConfig } from '../components/heading';
import { Heading } from '../services/heading.service';
import { Carousel } from 'react-responsive-carousel';
import { RankingChart } from '../components/ranking-chart';
import { TeamStatsAdapter } from '../services/team-stats-adapter';
import { TwoSideBarChart } from '../components/two-side-bar-chart';

type ScoreBoardState = {
  teamAvgGoalsPerSet: ChartData2D<string, number>
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
      { title: '⌀ Anzahl Tore pro Satz', iconPath: '/img/goal.png' },
      { title: 'Torverhältnis', iconPath: '/img/goal.png' },
      { title: 'Schießt Tor nach', iconPath: '/img/stopwatch.png' },
    ];

    this.updateList();
  }

  private updateList(): void {
    TeamStatsAdapter.getTeamStatsChartData().then(
      ({teamAvgGoalsPerSet, teamGoalRatio, teamTimeToScore}) =>
      this.setState({teamAvgGoalsPerSet, teamGoalRatio, teamTimeToScore})
    );
  }

  private updateHeading(index: number): void {
    Heading.Service.update(this.headings[index]);
  }

  private calculateGoalRatio(a: number,b: number): number {
      if (a === 0 && b !== 0)
        return b
      else if (a === 0)
        return 0
      else
        return +(b / a).toFixed(2)
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
          interval={ 10000 }
          showStatus={ false }
          showArrows={ false }
        >
          <div className="chart-container">
            <div className="fading-top" />
            <RankingChart data={ !this.state ? undefined : this.state.teamAvgGoalsPerSet } />
          </div>

          <div className="chart-container">
            <div className="fading-top" />
            <TwoSideBarChart
              data = { !this.state ? undefined : this.state.teamGoalRatio }
              cumulationHeadline = "Torverhältnis"
              cumulate = { this.calculateGoalRatio }
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
