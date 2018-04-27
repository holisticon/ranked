import axios from 'axios';
import { ChartData2D, ChartData3D } from '../types';

export namespace TeamStatsAdapter {

  export type TeamStats = {
    name: string
    goalsScored: number,
    goalsConceded: number,
    avgGoalTime: number
  }

  function getTeamStats(): Promise<Array<TeamStats>> {
    return axios.get('view/team/stats').then(res => res.data);
  }

  export function getTeamStatsChartData(): Promise<{
    teamGoalRatio: ChartData3D<string, number, number>,
    teamTimeToScore: ChartData2D<string, number> }>
  {
    return getTeamStats().then( teamStats => {

      const teamGoalRatio: ChartData3D<string, number, number> = {
        dimensions: [{ description: 'Team' }, { description: 'Kassiert' }, { description: 'Geschossen' }],
        entries: []
      };
      const teamTimeToScore: ChartData2D<string, number>  = {
        dimensions: [{ description: 'Team' }, { description: 'Schießt Tor nach' }],
        entries: []
      };

      teamStats.forEach(teamStat => {
        teamGoalRatio.entries.push(
          [teamStat.name, teamStat.goalsConceded, teamStat.goalsScored]
        )

        teamTimeToScore.entries.push(
          [teamStat.name, +teamStat.avgGoalTime.toFixed(2)]
        )
      });

      const ratio = (val: Array<any>) => val[1] === 0 ? 0 : val[2]/val[1]

      teamGoalRatio.entries.sort( (a, b) => ratio(b) - ratio(a));
      teamTimeToScore.entries.sort((a, b) => a[1] - b[1]);

      return {teamGoalRatio, teamTimeToScore}
      }
    )
  }
}



