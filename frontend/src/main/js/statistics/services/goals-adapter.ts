import axios from 'axios';
import { ChartData2D, ChartData3D, GoalStatistics } from '../types';
import { Player } from '../../types/types';
import { PlayersAdapter } from './players-adapter';

export namespace GoalsAdapter {

  type GoalsCount = {
    whenInOffense: number,
    whenInDefense: number
  };

  type GoalStats = {
    goalTimeAverage: number,
    goalsScored: GoalsCount,
    goalsConceded: GoalsCount
  };

  type PlayerGoals = {
    userName: {
      value: string
    },
    goalsScored: GoalsCount,
    goalsConceded: GoalsCount
  };

  type PlayerScoreTime = {
    userName: {
      value: string
    },
    goalTime: number
  };

  function total(goals: GoalsCount): number {
    return goals.whenInDefense + goals.whenInOffense;
  }

  function getPlayerGoalsCount(): Promise<Array<PlayerGoals>> {
    return axios.get('/view/goals/count')
      .then(res => res.data);
  }

  function getPlayerAvgScoreTime(): Promise<Array<PlayerScoreTime>> {
    return axios.get('/view/goals/time/average')
      .then(res => res.data);
  }

  export function getTotalGoalsData(): Promise<ChartData2D<Player, string>> {
    return Promise.all([getPlayerGoalsCount(), PlayersAdapter.getPlayersMap()])
      .then(([goalsData, playersMap]) => {
        const playerElos: ChartData2D<Player, string> = {
          dimensions: [{ description: 'Player' }, { description: 'Total goals count' }],
          entries: goalsData.map(goals => {
            return [
              playersMap[goals.userName.value],
              (total(goals.goalsScored) / total(goals.goalsConceded)).toFixed(2)
            ] as [Player, string];
          }).sort((a, b) => +b[1] - +a[1])
        };

        return playerElos;
      });
  }

  export function getConcededScoredGoalsData(): Promise<ChartData3D<Player, number, number>> {
    return Promise.all([getPlayerGoalsCount(), PlayersAdapter.getPlayersMap()])
      .then(([goalsData, playersMap]) => {
        const playerGoals: ChartData3D<Player, number, number> = {
          dimensions: [{ description: 'Spieler' }, { description: 'Kassiert' }, { description: 'Geschossen' }],
          entries: goalsData.map(goals => {
            return [
              playersMap[goals.userName.value],
              total(goals.goalsConceded),
              total(goals.goalsScored)
            ] as [Player, number, number];
          }).sort((a, b) => (b[2] / b[1]) - (a[2] / a[1]))
        };

        return playerGoals;
      });
  }

  export function getPlayerPositionGoalsData(): Promise<ChartData3D<Player, number, number>> {
    return Promise.all([getPlayerGoalsCount(), PlayersAdapter.getPlayersMap()])
      .then(([goalsData, playersMap]) => {
        const playerGoals: ChartData3D<Player, number, number> = {
          dimensions: [{ description: 'Spieler' }, { description: 'In Angriff' }, { description: 'In Verteidigung' }],
          entries: goalsData.map(goals => {
            return [
              playersMap[goals.userName.value],
              goals.goalsScored.whenInOffense,
              goals.goalsScored.whenInDefense
            ] as [Player, number, number];
          }).sort((a, b) => (b[1] + b[2]) - (a[1] + a[2]))
        };

        return playerGoals;
      });
  }

  export function getPlayerAvgScoreTimeData(): Promise<ChartData2D<Player, number>> {
    return Promise.all([getPlayerAvgScoreTime(), PlayersAdapter.getPlayersMap()])
      .then(([avgScoreTimes, playersMap]) => {
        const playerAvgScoreTime: ChartData2D<Player, number> = {
          dimensions: [{description: 'Player'}, {description: 'Average time to score', unit: 's'}],
          entries: avgScoreTimes.map(avgScoreTime => {
            return [
              playersMap[avgScoreTime.userName.value],
              avgScoreTime.goalTime
            ] as [Player, number];
          })
        };

        return playerAvgScoreTime;
      });
  }

  export function getGoalStatsForPlayer(playerName: string): Promise<GoalStatistics> {
    return axios.get('/view/goals/player/' + playerName)
      .then(res => res.data)
      .then((data: GoalStats) => {
        return {
          scored: total(data.goalsScored),
          ratio: total(data.goalsScored) / total(data.goalsConceded),
          avgTimeToScore: data.goalTimeAverage
        } as GoalStatistics;
      });
  }
}
