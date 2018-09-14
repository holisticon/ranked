import axios from 'axios';

import { Player, PlayerData, Team, TeamData } from '../types/types';

export namespace PlayerService {
  const backend = 'http://docker.holisticon.local:11082';

  export function getAllPlayers(): Promise<Array<Player>> {
    return axios.get(backend + '/view/player')
      .then(res => (res.data as Array<PlayerData>).map(data => new Player(data)));
  }

  export function getPlayer(playerId: string): Promise<Player> {
    return axios.get(backend + '/view/player/' + playerId)
      .then(res => new Player(res.data));
  }

  /**
   * Nur für das Turnier beim Werkstättchen
   * 
   * var elos = {};
   * temp1.forEach(p => elos[p.userName.value] = p.elo);
   * JSON.stringify(elos);
   */
  export function getCurrentEloRanking(): { [playerId: string]: number } {
    return {
      'romanschloemmer': 1080, 'dierkharbeck': 1073, 'carstensahling': 1062, 'oliverniebsch': 1050, 'jangalinski': 1036,
      'oliverihns': 1033, 'ennothieleke': 1024, 'tobiasbehr': 1022, 'nilsernsting': 1017, 'christianweinert': 1012,
      'patrickschalk': 1007, 'stefanzilske': 1005, 'joehm': 1005, 'martinguenther': 1005, 'julianwrastil': 1005,
      'oliverochs': 1004, 'robinpommerenke': 1004, 'mechtildkniesburges': 1004, 'stefanmerkl': 1004,
      'simonspruenker': 1002, 'pauldeuster': 1001, 'franziskaknorr': 1001, 'maltesoerensen': 996,
      'christianneuenstadt': 996, 'inagalinsky': 996, 'klausobst': 996, 'catherinecolombo': 995, 'jochenmeyer': 995,
      'michaelfritsch': 993, 'timogroeger': 992, 'stefanheldt': 992, 'erikhogrefe': 992, 'jessicakampmann': 992,
      'leonfausten': 985, 'ferhatayaz': 984, 'christophwolff': 981, 'christophgerkens': 976, 'danielsteinhoefer': 970,
      'simonzambrovski': 970, 'tobiasstamann': 969, 'detlefvonderthuesen': 964, 'lukastaake': 960, 'wiebkedahl': 959,
      'thorstenrahlf': 950, 'simonnehls': 946
    };
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
