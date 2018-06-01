import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { Player, Team } from '../../types/types';
import { push } from 'react-router-redux';
import { PartialStoreState } from '../store.state';
import './tournament-administration.css';
import { PlayerIcon } from '../../components/player-icon';
import { PlayerService } from '../../services/player-service';

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

function createTeamName(player1Name: string, player2Name: string): string {
  return player1Name.substr(0, 3) + player2Name.substr(0, 3);
}

function buildTeams(players: Array<Player>): Array<Team> {
  const teams: Array<Team> = [];

  if (players.length === 32) {
    // for now we just support a tournament with 16 teams
    for (let i = 0; i < players.length; i += 2) {
      let p1 = players[i];
      let p2 = players[i + 1];

      teams.push({
        name: createTeamName(p1.displayName, p2.displayName),
        player1: p1,
        player2: p2,
        wonSets: 0,
        imageUrl: ''
      });
    }
  }

  return teams;
}

function tournamentAdmin({ participants, addPlayer, removePlayer, startTournament }: InternalTournamentAdminProps) {
  const tournamentReady = participants.length === 32;

  const buildTeamsAndStartTournament = () => {
    if (tournamentReady) {
      const createTeamPromises = buildTeams(participants).map(PlayerService.createTeam);
      Promise.all(createTeamPromises).then(() => {
        startTournament();
      }).catch(err => {
        alert('Unerwarteter Fehler!/n' + JSON.stringify(err));
      });
    }
  };

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
        onClick={ () => buildTeamsAndStartTournament() }
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
      dispatch(push('/tournament'));
    }
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(tournamentAdmin);
