import axios from 'axios';

export namespace Config {
  interface BackendConfig {
    scoreToWinSet: number,
    setsToWinMatch: number,
  }

  export let pointsPerMatch: number;
  export let pointsPerSet: number;
  export const teamMode = true;
  // const BACKEND_URL = 'http://localhost:8080';

  export function initConfig(): Promise<void> {
    return axios.get('/view/configuration')
      .then(res => res.data)
      .then((config: BackendConfig) =>
        {
          pointsPerSet = config.scoreToWinSet;
          pointsPerMatch = config.setsToWinMatch;
          return;
        });
  }
}
