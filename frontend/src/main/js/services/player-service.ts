import axios from 'axios';
import { Player } from '../types/types';

export namespace PlayerService {
  export function getAllPlayers(): Promise<Array<Player>> {
    return axios.get('/view/user')
      .then(res => res.data);
  }
}