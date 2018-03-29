import axios from 'axios';
import { Player, PlayerData } from '../types/types';

export namespace PlayerService {
  export function getAllPlayers(): Promise<Array<Player>> {
    return axios.get('/view/player')
      .then(res => (res.data as Array<PlayerData>).map(data => new Player(data)));
  }
}