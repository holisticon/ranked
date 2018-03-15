import * as React from 'react';
import './player-icon.css';

export interface PlayerIconProps {
  click: () => void;
  img: string;
  name?: string;
}

interface PlayerIconState {
  showAltImage: boolean;
}

export class PlayerIcon extends React.Component<PlayerIconProps, PlayerIconState> {
  constructor(props: PlayerIconProps) {
    super(props);
    this.state = { showAltImage: !props.img };
  }

  private showAltImage(): void {
    this.setState({ showAltImage: true });
  }

  private getInitials(): string {
    return (this.props.name || '').split('').filter(letter => {
      const code = letter.charCodeAt(0);
      return code > 32 && code < 97;
    }).join('');
  }

  render() {
    return (
      <div className="player-icon" onClick={ this.props.click }>
        <div className="player-image">
          {
            this.state.showAltImage ?
            <div className="placeholder"><span>{ this.getInitials() }</span></div> :
            <img src={ this.props.img } onError={ () => this.showAltImage() } />
          }
        </div>
      </div>
    );
  }
}