import * as React from 'react';
import './player-icon.css';
import { Config } from '../config';

export interface PlayerIconProps {
  click: () => void;
  img: string;
  name?: string;
}

interface PlayerIconState {
  imageNotFound: boolean;
}

export class PlayerIcon extends React.Component<PlayerIconProps, PlayerIconState> {
  constructor(props: PlayerIconProps) {
    super(props);
    this.state = { imageNotFound: false };
  }

  componentWillReceiveProps(): void {
    this.setState({ imageNotFound: false });
  }

  private showAltImage(): void {
    this.setState({ imageNotFound: true });
  }

  private getInitials(): string {
    return (this.props.name || '').split('').filter(letter => {
      const code = letter.charCodeAt(0);
      return code > 32 && code < 97;
    }).join('');
  }

  render() {
    const showAltImage = this.state.imageNotFound || !this.props.img;

    return (
      <div className="player-icon" onClick={ this.props.click }>
        <div className="player-image">
          {
            showAltImage && !Config.teamMode ?
            <div className="placeholder"><span>{ this.getInitials() }</span></div> :
            null
          }

          {
            showAltImage && Config.teamMode ?
            <i className="material-icons">&#xE7FB;</i> :
            null
          }

          {
            !showAltImage ?
            <img src={ this.props.img } onError={ () => this.showAltImage() } /> :
            null
          }
        </div>
      </div>
    );
  }
}
