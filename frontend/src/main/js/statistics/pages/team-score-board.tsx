import * as React from 'react';
import { ChartData2D, ChartData3D } from '../types';
import { HeadingComponent, HeadingConfig } from '../components/heading';
import { Heading } from '../services/heading.service';
import { Carousel } from 'react-responsive-carousel';
import { RankingChart } from '../components/ranking-chart';
import { TeamStatsAdapter } from '../services/team-stats-adapter';
import { TwoSideBarChart } from '../components/two-side-bar-chart';
import { Team } from '../../types/types';
import { PlayerService } from '../../services/player-service';
import { WallService } from '../../services/wall.service';

type TeamScoreBoardState = {
  teamAvgGoalsPerSet: ChartData2D<string, number>
  goalRatio: ChartData3D<Team, number, number>,
  timeToScore: ChartData2D<Team, number>
};

export class TeamScoreBoard extends React.Component<any, TeamScoreBoardState> {

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

    WallService.playedMatches().subscribe(_ => this.updateList());
  }

  private findByName(teams: Array<Team>, name: string): Team {
    return teams.find(t => name === t.name)!!;
  }

  private updateList(): void {
    Promise.all([TeamStatsAdapter.getTeamStatsChartData(), PlayerService.getAllTeams()]).then(
      ([{teamAvgGoalsPerSet, teamGoalRatio, teamTimeToScore}, teams]) => {
        const goalRatio = {
          dimensions: teamGoalRatio.dimensions,
          entries: teamGoalRatio.entries.map(entry => [this.findByName(teams, entry[0]), entry[1], entry[2]])
        } as ChartData3D<Team, number, number>;
        const timeToScore = {
          dimensions: teamTimeToScore.dimensions,
          entries: teamTimeToScore.entries.map(entry => [this.findByName(teams, entry[0]), entry[1]])
        } as ChartData2D<Team, number>;
        this.setState({teamAvgGoalsPerSet, goalRatio, timeToScore});
      }
    );
  }

  private updateHeading(index: number): void {
    Heading.Service.update(this.headings[index]);
  }

  private calculateGoalRatio(a: number, b: number): number {
      if (a === 0 && b !== 0) {
        return b;
      } else if (a === 0) {
        return 0;
      } else {
        return +(b / a).toFixed(2);
      }
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
              data={ !this.state ? undefined : this.state.goalRatio }
              cumulationHeadline="Torverhältnis"
              cumulate={ this.calculateGoalRatio }
            />
          </div>

          <div className="chart-container">
            <div className="fading-top" />
            <RankingChart data={ !this.state ? undefined : this.state.timeToScore } />
          </div>
        </Carousel>
      </div>
    );
  }

}
