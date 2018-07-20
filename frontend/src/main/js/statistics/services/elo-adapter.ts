import axios from 'axios';
import { ChartData2D } from '../types';
import { Player } from '../../types/types';
import { PlayersAdapter } from './players-adapter';

export namespace EloAdapter {

  type PlayerElo = {
    userName: {
      value: string;
    },
    elo: number;
  };

  function getPlayerElos(): Promise<Array<PlayerElo>> {
    return axios.get('/view/elo/player')
      .then(res => res.data);
  }

  export function getEloData(): Promise<ChartData2D<Player, number>> {
    return Promise.all([getPlayerElos(), PlayersAdapter.getPlayersMap()])
      .then(([eloData, playersMap]) => {
        const playerElos: ChartData2D<Player, number> = {
          dimensions: [{ description: 'Player' }, { description: 'Elo' }],
          entries: eloData.map(elo => {
            return [
              playersMap[elo.userName.value],
              elo.elo
            ] as [Player, number];
          })
        };

        return playerElos;
      });
  }

  type EloEntry = {
    first: string,
    second: number
  };

  function sameDay(date: Date | null, other: Date | null): boolean {
    if (!date || !other) {
      return false;
    }

    return date.getDate() === other.getDate() &&
            date.getMonth() === other.getMonth() &&
            date.getFullYear() === other.getFullYear();
  }

  export function getEloHistoryForPlayer(playerName: string): Promise<ChartData2D<Date, number>> {
    return axios.get('/view/elo/player/' + playerName)
      .then(res => res.data)
      .then((eloHistoryData: Array<EloEntry>) => {
        const eloHistory: ChartData2D<Date, number> = {
          dimensions: [{ description: 'Timestamp' }, { description: 'Elo' }],
          entries: eloHistoryData.map(entry => {
            return [new Date(entry.first), entry.second] as [Date, number];
          }).filter(([entryDate, _], i, history) => {
            let nextDate = (i + 1) < history.length ? history[i + 1][0] : null;
            return !sameDay(entryDate, nextDate);
          })
        };

        return eloHistory;
      });
  }
}