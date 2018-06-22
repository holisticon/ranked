import axios from 'axios';
import { Player, PlayerData, Team, TeamData } from '../types/types';

export namespace PlayerService {
  export function getAllPlayers(): Promise<Array<Player>> {
    return axios.get('/view/player')
      .then(res => (res.data as Array<PlayerData>).map(data => new Player(data)));
  }

  /**
   * Nur für das Turnier beim Werkstättchen
   * 
   * var elos = {};
   * temp1.forEach(p => elos[p.userName.value] = p.elo);
   * JSON.stringify(elos);
   */
  export function getCurrentEloRanking(): { [playerId: string]: number } {
    // tslint:disable-next-line:max-line-length
    return { 'romanschloemmer': 1084, 'dierkharbeck': 1073, 'carstensahling': 1067, 'oliverniebsch': 1041, 'jangalinski': 1036, 'oliverihns': 1033, 'ennothieleke': 1024, 'tobiasbehr': 1022, 'nilsernsting': 1017, 'patrickschalk': 1016, 'christianweinert': 1012, 'stefanzilske': 1005, 'joehm': 1005, 'martinguenther': 1005, 'julianwrastil': 1005, 'oliverochs': 1004, 'robinpommerenke': 1004, 'mechtildkniesburges': 1004, 'stefanmerkl': 1004, 'simonspruenker': 1002, 'pauldeuster': 1001, 'franziskaknorr': 1001, 'maltesoerensen': 996, 'christianneuenstadt': 996, 'inagalinsky': 996, 'klausobst': 996, 'catherinecolombo': 995, 'jochenmeyer': 995, 'timogroeger': 992, 'erikhogrefe': 992, 'jessicakampmann': 992, 'stefanheldt': 987, 'leonfausten': 985, 'ferhatayaz': 984, 'christophwolff': 981, 'michaelfritsch': 979, 'christophgerkens': 976, 'lukastaake': 970, 'danielsteinhoefer': 970, 'simonzambrovski': 970, 'tobiasstamann': 969, 'detlefvonderthuesen': 964, 'wiebkedahl': 959, 'thorstenrahlf': 950, 'simonnehls': 946 };
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
