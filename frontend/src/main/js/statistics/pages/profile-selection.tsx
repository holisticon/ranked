import * as React from 'react';
import './profile-selection.css';
import { HeadingComponent } from '../components/heading';
import { Player } from '../../types/types';
import { PlayerService } from '../../services/player-service';
import { Link } from 'react-router-dom';

type ProfileSelectionState = {
  players: Array<Player>
};

export class ProfileSelection extends React.Component<any, ProfileSelectionState> {
  constructor(props: any) {
    super(props);

    this.state = {} as ProfileSelectionState;
    this.initPlayers();
  }

  initPlayers(): void {
    PlayerService.getAllPlayers()
      .then(players => this.setState({ players }));
  }

  renderPlayers() {
    if (!this.state.players) {
      return;
    }

    return this.state.players.map((player, index) => {
      return (
        <Link key={ index } to={ '/profile/player/' + player.id }>
          <div className="player">
            <span className="name">{ player.displayName }</span>
            <div className="underline"/>
          </div>
        </Link>
      );
    });
  }

  render() {
    return (
      <div className="profile-selection">
        <HeadingComponent
          title="Steckbrief"
          iconPath="/img/profile.png"
        />
        <div className="player-list">
          { this.renderPlayers() }
        </div>
      </div>
    );
  }
}
