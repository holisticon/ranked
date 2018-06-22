import * as React from 'react';
import { Player } from '../types/types';
import { PlayerIcon } from './player-icon';
import { Link } from 'react-router-dom';
import './player-selection.css';

const alphabet = 'abcdefghijklmnopqrstuvwxyz';

export interface PlayerSelectionProps {
  selectedLetter?: string;
  urlPrefix?: string;
  availablePlayers: Array<Player>;
  markedPlayerIds: Array<string>;
  select: (selected: Player) => void;
}

export interface PlayerSelectionState {
}

export class PlayerSelectionComponent extends React.Component<PlayerSelectionProps, PlayerSelectionState> {

  constructor(props: PlayerSelectionProps) {
    super(props);
  }

  private getUnavailableLetters(): string {
    let unavailableLetters = alphabet;
    this.props.availablePlayers.forEach((player: Player) => {
      unavailableLetters = unavailableLetters.replace(player.displayName[0].toLowerCase(), '');
    });

    return unavailableLetters;
  }

  private getLetters() {
    return alphabet.split('').map((letter, index) => {

      const unavailableLetters = this.getUnavailableLetters();
      const available = !unavailableLetters.includes(letter);
      const urlPrefix = this.props.urlPrefix || '';

      return (
        <Link key={index} to={urlPrefix + '/select/' + letter}>
          <div className={available ? 'letter' : 'letter gray'}>
            <div className="letter-content">
              <div className="letter-absolute">{letter.toUpperCase()}</div>
            </div>
          </div>
        </Link>
      );
    });
  }

  private getPlayerIcons() {
    const players = !this.props.selectedLetter ? this.props.availablePlayers :
      this.props.availablePlayers.filter(player => player.displayName[0].toLowerCase() === this.props.selectedLetter);

    return players.map((player, index) => {
      const marked = !!this.props.markedPlayerIds.find(id => player.id === id);
      return (
        <div key={index} className={ marked ? 'player marked' : 'player' }>
          <PlayerIcon
            img={player.imageUrl}
            name={player.displayName}
            click={() => this.props.select(player)}
          />
          <div className="marked-container">
            <div className="corner" />
            <i className="material-icons">check</i>
          </div>
        </div>
      );
    });
  }

  public render() {
    return (
      <div className={'player-selection'}>
        {!this.props.selectedLetter ?
          this.getLetters() :
          this.getPlayerIcons()}
      </div>
    );
  }
}