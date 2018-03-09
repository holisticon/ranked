import * as React from 'react';
import axios from 'axios';
import './score-board.css';
import { Player } from '../types/types';
import { PlayerIcon } from '../components/player-icon';

type EloData = {
  userName: {
    value: string;
  },
  elo: number;
};

interface PlayerWithElo extends Player {
  score: number;
}

type ScoreBoardState = {
  players: Array<PlayerWithElo>,
  maxElo: number,
  minElo: number
};

export class ScoreBoard extends React.Component<any, ScoreBoardState> {
  constructor(props: any) {
    super(props);

    // init data
    this.getAllPlayers().then(playersMap => {
      this.getPlayersScore().then(eloData => {
        const players = eloData.map(elo => {
          return {
            ...playersMap[elo.userName.value],
            score: elo.elo
          };
        });

        this.setState({
          players,
          maxElo: eloData[0].elo,
          minElo: eloData[eloData.length - 1].elo
        });
      });
    });
  }

  private getAllPlayers(): Promise<{ [id: string]: Player }> {
    return axios.get('/view/user')
      .then(res => res.data as Array<Player>)
      .then(players => players.reduce(
        (map, player) => {
          map[player.id] = player;
          return map;
        },
        {})
      );
  }

  private getPlayersScore(): Promise<Array<EloData>> {
    return axios.get('/view/elo/player')
      .then(res => res.data);
  }

  private calcEloPercentage(elo: number): number {
    const min = this.state.minElo;
    const max = this.state.maxElo;

    return (elo - min) / (max - min);
  }

  private getRanking() {
    if (!this.state || !this.state.players) {
      return;
    }

    return this.state.players
      .map((player, i) => {
        const barWidth = this.calcEloPercentage(player.score) * 70 + 30;

        return (
          <div key="i" className="ranking-entry">
            <div className="icon">
              <PlayerIcon img={player.imageUrl} click={() => { return; }} />
            </div>
            <div className="name">{player.name}</div>
            <div className="bar">
              <div className="bar-inner" style={{ width: barWidth + '%' }} />
            </div>
            <div className="score">{player.score}</div>
          </div>
        );
      });
  }

  private getHeaderIconPath(): string {
    return '/img/trophy.png';
  }

  private getTitle(): string {
    return 'Holisticon AllStars';
  }

  public render() {
    return (
      <div className="score-board">
        <div className="header">
          <div className="background" />
          <div className="icon" style={{ backgroundImage: `url(${this.getHeaderIconPath()})` }} />
          <div className="title">{this.getTitle()}</div>
        </div>
        <div className="ranking-list">
          <div className="fading-top" />
          {this.getRanking()}
        </div>
      </div>
    );
  }
}