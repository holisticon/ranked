import axios from 'axios';
import { Player, PlayerData, Team } from '../types/types';

export namespace PlayerService {
  export function getAllPlayers(): Promise<Array<Player>> {
    return axios.get('/view/player')
      .then(res => (res.data as Array<PlayerData>).map(data => new Player(data)));
  }

  export function getAllTeams(): Array<Team> {
    return [
      { player1: {id: "player1", displayName: "player1", imageUrl: "_", userName: {value: "player1"}},
        player2: {id: "player2", displayName: "player2", imageUrl: "_", userName: {value: "player2"}},
        name: "Holis",
        wonSets: 0 },

      { player1: {id: "player1", displayName: "player1", imageUrl: "_", userName: {value: "player1"}},
        player2: {id: "player2", displayName: "player2", imageUrl: "_", userName: {value: "player2"}},
        name: "Könner",
        wonSets: 0 }
      ]
  }
}
