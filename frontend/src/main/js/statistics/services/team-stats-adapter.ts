import axios from 'axios';
import { ChartData2D, ChartData3D } from '../types';

export namespace TeamStatsAdapter {

  export type TeamStats = {
    name: string
    goalsScored: number,
    goalsConceded: number,
    avgGoalTime: number,
    avgGoalsPerSet: number
  };

  function getTeamStats(): Promise<Array<TeamStats>> {
    return axios.get('view/team/stats').then(res => res.data);
  }

  export function getTeamStatsChartData(): Promise<{
    teamAvgGoalsPerSet: ChartData2D<string, number>,
    teamGoalRatio: ChartData3D<string, number, number>,
    teamTimeToScore: ChartData2D<string, number>
  }> {
    return getTeamStats().then( teamStats => {
      const teamAvgGoalsPerSet: ChartData2D<string, number>  = {
        dimensions: [{ description: 'Team' }, { description: '⌀ Anzahl Tore pro Satz'}],
        entries: [],
      };

      const teamGoalRatio: ChartData3D<string, number, number> = {
        dimensions: [{ description: 'Team' }, { description: 'Kassiert' }, { description: 'Geschossen' }],
        entries: []
      };
      const teamTimeToScore: ChartData2D<string, number>  = {
        dimensions: [{ description: 'Team' }, { description: 'Schießt Tor nach', unit: 's' }],
        entries: []
      };

      teamStats.forEach(teamStat => {
        teamAvgGoalsPerSet.entries.push(
          [teamStat.name, +teamStat.avgGoalsPerSet.toFixed(2)]
        );

        teamGoalRatio.entries.push(
          [teamStat.name, teamStat.goalsConceded, teamStat.goalsScored]
        );

        teamTimeToScore.entries.push(
          [teamStat.name, +teamStat.avgGoalTime.toFixed(2)]
        );
      });

      const ratio = (val: Array<any>): number => {
        if (val[1] === 0 && val[2] !== 0) {
          return val[2];
        } else if (val[1] === 0) {
          return 0;
        } else {
          return val[2] / val[1];
        }
      };

      teamAvgGoalsPerSet.entries.sort((a, b) => b[1] - a[1]);
      teamGoalRatio.entries.sort( (a, b) => ratio(b) - ratio(a));
      teamTimeToScore.entries.sort((a, b) => a[1] - b[1]);

      return { teamAvgGoalsPerSet, teamGoalRatio, teamTimeToScore };
      }
    );
  }
}
