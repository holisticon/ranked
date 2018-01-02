import React from 'react';
import ReactDOM from 'react-dom';
import { Match } from './pages/match';

class Ranked extends React.Component {
  constructor() {
    super();

    this.createTeams();
  }

  createTeams() {
    // fixme: mit sinnvollem code befüllen
    this.teams = {
      blue: { player1: 'Carsten', player2: 'Lukas' },
      red: { player1: 'Olli', player2: 'Timo' },
    }
  }

  render() {
    return (
      <div className="ranked">
        <Match teams={ this.teams } />
      </div>
    );
  }
}

ReactDOM.render(
  <Ranked />,
  document.getElementById('root')
);
