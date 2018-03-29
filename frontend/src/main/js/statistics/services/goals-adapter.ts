import axios from 'axios';
import { ChartData2D } from '../types';
import { Player } from '../../types/types';
import { PlayersAdapter } from './players-adapter';

export namespace GoalsAdapter {

  type PlayerGoals = {
    userName: {
      value: string;
    },
    goals: number;
  };

  function getTotalPlayerGoals(): Promise<Array<PlayerGoals>> {
    return axios.get('/view/goals/sum')
      .then(res => res.data);
  }

  export function getTotalGoalsData(): Promise<ChartData2D<Player, number>> {
    return Promise.all([getTotalPlayerGoals(), PlayersAdapter.getPlayersMap()])
      .then(([goalsData, playersMap]) => {
        const playerElos: ChartData2D<Player, number> = {
          dimensions: [{ description: 'Player' }, { description: 'Elo' }],
          entries: goalsData.map(goals => {
            return [
              playersMap[goals.userName.value],
              goals.goals
            ] as [Player, number];
          })
        };

        return playerElos;
      });
  }

}