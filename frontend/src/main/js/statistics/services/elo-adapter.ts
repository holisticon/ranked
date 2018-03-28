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
}