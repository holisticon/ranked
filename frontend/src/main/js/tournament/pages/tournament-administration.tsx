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
  return player1Name.substr(0, 3).trim() + player2Name.substr(0, 3).trim();
}

function groupPlayerBasedOnElos(players: Array<Player>, elos: {[playerId: string]: number}): Array<Array<Player>> {
  const sorted = players.sort((p1, p2) => elos[p2.id] - elos[p1.id]);
  const firstAndLastTeamSize = (Math.floor(players.length / 4) % 2 === 0 ? 0 : 2) + (players.length % 4 === 0 ? 0 : 1);

  const groups = [] as Array<Array<Player>>;

  if (firstAndLastTeamSize > 0) {
    groups.push(sorted.slice(0, firstAndLastTeamSize));
  }

  for (let i = firstAndLastTeamSize; i < sorted.length - firstAndLastTeamSize; i += 4) {
    groups.push(sorted.slice(i, i + 4));
  }

  if (firstAndLastTeamSize > 0) {
    groups.push(sorted.slice(-firstAndLastTeamSize));
  }

  return groups;
}

function rnd(max: number): number {
  return Math.floor(Math.random() * max);
}

function buildTeams(players: Array<Player>): Array<Team> {
  const teams: Array<Team> = [];

  if (players.length % 2 === 0) {
    const playerGroups = groupPlayerBasedOnElos(players, PlayerService.getCurrentEloRanking());

    for (let index = 0; index < playerGroups.length / 2; index++) {
      let group = playerGroups[index];
      let reverseGroup = playerGroups[playerGroups.length - index - 1];
      group.forEach(p1 => {
        let p2Index = rnd(reverseGroup.length);
        let p2 = reverseGroup[p2Index];
        reverseGroup.splice(p2Index, 1);

        teams.push({
          name: createTeamName(p1.displayName, p2.displayName),
          player1: p1,
          player2: p2,
          wonSets: 0,
          imageUrl: ''
        });
      });
    }
  }

  return teams;
}

function createTeamsRecursive(teams: Array<Team>, index: number = 0): Promise<void> {
  if (index >= teams.length) {
    return Promise.resolve();
  }

  return PlayerService.createTeam(teams[index])
    .then(() => createTeamsRecursive(teams, index + 1))
    .catch(err => {
      alert('Unerwarteter Fehler!/n' + err.json());
    });
}

let showButton = false;
function handleKeyDown(event: KeyboardEvent): void {
  if (event.ctrlKey && event.key === 'k') {
    showButton = true;
    removeKeyDownHandler();
  }
}

let handlerAdded: boolean = false;

function addKeyDownHandler(): void {
  if (!handlerAdded) {
    document.addEventListener('keydown', handleKeyDown);
    handlerAdded = true;
  }
}

function removeKeyDownHandler(): void {
  if (handlerAdded) {
    document.removeEventListener('keydown', handleKeyDown);
    handlerAdded = false;
  }
}

function tournamentAdmin({ participants, addPlayer, removePlayer, startTournament }: InternalTournamentAdminProps) {
  const tournamentFull = participants.length === 32;

  addKeyDownHandler();

  const buildTeamsAndStartTournament = () => {
    createTeamsRecursive(buildTeams(participants))
      .then(() => startTournament());
  };

  return (
    <div className="tournament-admin-panel">
      <div className="participants">
        {
          tournamentFull ?
          null :
          <div className="add-player" onClick={ () => addPlayer() }>
            <div className="icon-container">
              <i className="material-icons">add</i>
            </div>
          </div>
        }
        { renderPlayerIcons(participants, removePlayer) }
      </div>

      {
        (showButton || tournamentFull) &&
        <div
          className={'button'}
          onClick={ () => buildTeamsAndStartTournament() }
        >
          <span>Turnier starten</span>
        </div>
      }

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
