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
          wonPercent: data.wonMatches * 100 / totalMatches,
          played: totalMatches,
          avgTime: data.averageMatchTime
        } as GameStatistics;
      });
  }
}