import * as React from 'react';
import { TournamentTree } from './tournament/pages/tournament-tree';
import { TeamScoreBoard } from './statistics/pages/team-score-board';
import './seacon.css';

export class Seacon extends React.Component<any, any> {
  constructor(props: any) {
    super(props);
  }

  render() {
    return (
      <div className="seacon">
        <div className="container">
          <TournamentTree />
        </div>
        <div className="container">
          <TeamScoreBoard />
        </div>
      </div>
    );
  }
}
