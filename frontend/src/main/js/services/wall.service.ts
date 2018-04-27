import axios from 'axios';
import { Observable, Observer } from 'rxjs';
import { PlayedMatch } from '../types/types';
import { BackendData } from '../types/backend-types';

export namespace WallService {

  let alreadyPlayedMatches: number = 0;
  const matchWall: Observable<Array<BackendData.Match>> = Observable
    .create((observer: Observer<Array<BackendData.Match>>) => {
      setInterval(
        () => {
          axios.get('/view/wall/matches')
            .then(res => (res.data as Array<BackendData.Match>))
            .then(matches => {
              if (matches.length > alreadyPlayedMatches) {
                observer.next(matches.slice(alreadyPlayedMatches));
                alreadyPlayedMatches = matches.length;
              }
            });
        },
        1000);
      observer.next([]);
    });

  export function playedMatches(): Observable<Array<PlayedMatch>> {
    return matchWall.map(matches => matches.map(match => ({
      team1: {
        player1: match.teamBlue.player1.value,
        player2: match.teamBlue.player2.value
      },
      team2: {
        player1: match.teamRed.player1.value,
        player2: match.teamRed.player2.value
      },
      winner: 'team1'
    }) as PlayedMatch));
  }
}
