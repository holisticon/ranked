import { Player } from '../../types/types';
import { PlayerService } from '../../services/player-service';

export namespace PlayersAdapter {

  let playersCache: { [id: string]: Player };
  
  export function getPlayersMap(): Promise<{ [id: string]: Player }> {
    if (!playersCache) {
      playersCache = {};
      return PlayerService.getAllPlayers()
        .then(players => {
          players.forEach(player => {
            playersCache[player.id] = player;
          });
          return playersCache;
        });
    } else {
      return Promise.resolve(playersCache);
    }
  }
}