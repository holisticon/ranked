import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { Player } from '../../types/types';
import { push } from 'react-router-redux';
import { PartialStoreState } from '../store.state';
import './tournament-administration.css';
import { PlayerIcon } from '../../components/player-icon';

interface InternalTournamentAdminProps {
  participants: Array<Player>;
  addPlayer: () => void;
  removePlayer: (player: Player) => void;
  startTournament: () => void;
}

/*function stopEvent(event: React.SyntheticEvent<Object>): boolean {
  event.preventDefault();
  event.stopPropagation();
  return true;
}*/

function renderPlayerIcons(players: Array<Player>, removePlayer: (player: Player) => void) {
  return players.map((player, index) => {
    return (
      <div key={ index } className="participant" onClick={ () => removePlayer(player) }>
        <PlayerIcon
          img={ player.imageUrl }
          name={ player.displayName }
          click={ () => { return; } }
        />
        <div className="remove-player">
          <div className="corner">
            <i className="material-icons">clear</i>
          </div>
        </div>
      </div>
    );
  });
}

function tournamentAdmin({ participants, addPlayer, removePlayer, startTournament }: InternalTournamentAdminProps) {
  const tournamentReady = participants.length === 32;

  return (
    <div className="tournament-admin-panel">
      <div className="participants">
        {
          tournamentReady ?
          null :
          <div className="add-player" onClick={ () => addPlayer() }>
            <div className="icon-container">
              <i className="material-icons">add</i>
            </div>
          </div>
        }
        { renderPlayerIcons(participants, removePlayer) }
      </div>

      <div
        className={'button' + (tournamentReady ? '' : ' disabled')}
        onClick={ () => tournamentReady && startTournament() }
      >
        <span>Turnier starten</span>
      </div>

      <div className="footer">
        <div className="dome" />
        <span className="tag">#ranked</span>
      </div>
    </div>
  );
}

export function mapStateToProps({tournament: store}: PartialStoreState) {
  return {
    participants: store.tournamentParticipants
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.TournamentAction>) {
  return {
    addPlayer: () => {
      dispatch(push('/tournamentAdmin/select'));
    },
    removePlayer: (player: Player) => {
      dispatch(Actions.removeParticipant(player));
    },
    startTournament: () => {
      return;
    }
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(tournamentAdmin);
