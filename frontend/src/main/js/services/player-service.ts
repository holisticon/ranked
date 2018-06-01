import axios from 'axios';
import { Player, PlayerData, Team, TeamData } from '../types/types';

export namespace PlayerService {
  export function getAllPlayers(): Promise<Array<Player>> {
    return axios.get('/view/player')
      .then(res => (res.data as Array<PlayerData>).map(data => new Player(data)));
  }

  export function getAllTeams(): Promise<Array<Team>> {
    return axios.get('/view/teams')
      .then(res => (res.data as Array<TeamData>).map(data => new Team(data)));
  }

  export function createTeam(team: Team): Promise<any> {
    return axios.post('/command/team', {
      name: team.name,
      imageUrl: team.imageUrl,
      player1Name: team.player1.userName.value,
      player2Name: team.player2.userName.value
    });
  }
}
