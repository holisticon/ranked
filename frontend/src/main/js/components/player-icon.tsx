import * as React from 'react';
import './player-icon.css';

export interface PlayerIconProps {
  click: () => void;
  img: string;
}

export class PlayerIcon extends React.Component<PlayerIconProps> {
  constructor(props: PlayerIconProps) {
    super(props);
  }

  render() {
    return (
      <div className="player-icon" onClick={ this.props.click }>
        <div className="player-image" style={ {backgroundImage: `url(${this.props.img})`} } />
      </div>
    );
  }
}