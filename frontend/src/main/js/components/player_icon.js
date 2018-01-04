import React from 'react';

export class PlayerIcon extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div className="player-icon" onClick={ this.props.onClick }>
        <div className="player-image" style={ {backgroundImage: `url(${this.props.img})`} }></div>
      </div>
    );
  }
}