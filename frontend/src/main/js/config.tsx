import axios from 'axios';

export namespace Config {
  interface BackendConfig {
    scoreToWinSet: number;
    setsToWinMatch: number;
  }

  export let pointsPerMatch: number = 2;
  export let pointsPerSet: number = 6;
  export const teamMode: boolean = false;
  export const showTeamName: boolean = true;
  // const BACKEND_URL = 'http://localhost:8080';

  export const timedMatchMode = false;
  export const timePerSet = 5;

  export const timeForManikinSelection = 5;

  export function initConfig(): Promise<void> {
    return axios.get('/view/configuration')
      .then(res => res.data)
      .then((config: BackendConfig) => {
          pointsPerSet = config.scoreToWinSet;
          pointsPerMatch = config.setsToWinMatch;
          return;
        });
  }
}
