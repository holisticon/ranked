import axios from 'axios';
import { SetStatistics } from '../types';

export namespace SetAdapter {

  type SetStats = {
    userName: {
      value: string;
    },
    wonSets: {
      whenInOffense: number,
      whenInDefense: number
    },
    lostSets: {
      whenInOffense: number,
      whenInDefense: number
    }
    averageSetTime: number,
  };

  export function getSetStatsForPlayer(playerName: string): Promise<SetStatistics> {
    return axios.get('/view/sets/player/' + playerName)
      .then(res => res.data)
      .then((data: SetStats) => {
        const totalSets = data.wonSets.whenInDefense + data.wonSets.whenInOffense +
                          data.lostSets.whenInDefense + data.lostSets.whenInOffense;
        return {
          wonPercent: (data.wonSets.whenInDefense + data.wonSets.whenInOffense) * 100 / totalSets,
          played: totalSets,
          avgTime: data.averageSetTime
        } as SetStatistics;
      });
  }
}