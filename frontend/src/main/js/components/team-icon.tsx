import * as React from 'react';
import { Team } from '../types/types';
import { PlayerIcon } from './player-icon';
import './team-icon.css';

export interface TeamIconProps {
  team: Team;
}

export class TeamIcon extends React.Component<TeamIconProps, any> {
  constructor(props: TeamIconProps) {
    super(props);
  }

  render() {
    if (!this.props.team.imageUrl) {
      return (
        <div className="team-icon">
          <div className="half">
            <PlayerIcon
              click={() => { return; } }
              img={ this.props.team.player1.imageUrl }
            />
          </div>
          <div className="half">
            <PlayerIcon
              click={() => { return; } }
              img={ this.props.team.player2.imageUrl }
            />
          </div>
        </div>
      );
    } else {
      return (
        <PlayerIcon click={() => { return; } } img={ this.props.team.imageUrl } />
      );
    }
  }
}
