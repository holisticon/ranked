import axios from 'axios';
import { GameStatistics } from '../types';

export namespace MatchAdapter {

  type MatchStats = {
    userName: {
      value: string;
    },
    averageMatchTime: number,
    lostMatches: number,
    wonMatches: number
  };

  export function getMatchStatsForPlayer(playerName: string): Promise<GameStatistics> {
    return axios.get('/view/matches/player/' + playerName)
      .then(res => res.data)
      .then((data: MatchStats) => {
        const totalMatches = data.wonMatches + data.lostMatches;
        return {
          wonPercent: totalMatches > 0 ? (data.wonMatches * 100 / totalMatches) : 0,
          played: totalMatches,
          avgTime: data.averageMatchTime
        } as GameStatistics;
      });
  }
}