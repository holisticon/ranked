import axios from 'axios';
import { ChartData2D } from '../types';
import { Player } from '../../types/types';
import { PlayersAdapter } from './players-adapter';

export namespace GoalsAdapter {

  type GoalsCount = {
    whenInOffense: number,
    whenInDefense: number
  };

  type PlayerGoals = {
    userName: {
      value: string
    },
    goalsScored: GoalsCount,
    goalsConceded: GoalsCount
  };

  function total(goals: GoalsCount): number {
    return goals.whenInDefense + goals.whenInOffense;
  }

  function getPlayerGoals(): Promise<Array<PlayerGoals>> {
    return axios.get('/view/goals/count')
      .then(res => res.data);
  }

  export function getTotalGoalsData(): Promise<ChartData2D<Player, string>> {
    return Promise.all([getPlayerGoals(), PlayersAdapter.getPlayersMap()])
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

}