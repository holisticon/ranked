import * as React from 'react';
import axios from 'axios';
import './score-board.css';

type PlayerWithElo = {
    userName: {
        value: string;
    },
    elo: number;
};

export class ScoreBoard extends React.Component<any, { players: Array<PlayerWithElo> }> {
  constructor(props: any) {
    super(props);

    this.getPlayersScore();
  }

  getPlayersScore(): void {
    axios.get('/view/elo/player').then(res => this.setState({ players: res.data }));
  }

  getRanking() {
      if (!this.state || !this.state.players) {
          return;
      }

      return this.state.players
        .filter(player => player.elo !== 1000)
        .map((player: PlayerWithElo, i: number) => {
          return (
              <div key="i">
                  <span>{ player.elo + ' - ' +  player.userName.value }</span>
              </div>
          );
      });
  }

  render() {
    return (
      <div className="score-board">
        <span className="title">Die Besten der Besten:</span>
        { this.getRanking() }
      </div>
    );
  }
}