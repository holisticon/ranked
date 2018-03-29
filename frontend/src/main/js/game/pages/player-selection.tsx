import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { Player, PlayerKey, TeamKey } from '../../types/types';
import { PlayerIcon } from '../../components/player-icon';
import { Link } from 'react-router-dom';
import { match as Match } from 'react-router';
import { push } from 'react-router-redux';
import './player-selection.css';
import { PlayerService } from '../../services/player-service';

const alphabet = 'abcdefghijklmnopqrstuvwxyz';

export interface PlayerSelectionProps {
  match?: Match<any>;
  unavailableLetters: string;
  availablePlayers: Array<Player>;
  selectPlayerFor: any;
  select: (team: TeamKey, player: PlayerKey, selected: Player) => void;
  updateAvailablePlayers: (players: Array<Player>) => void;
}

function getLetters(unavailableLetters: string) {
  return alphabet.split('').map((letter, index) => {

    const available = !unavailableLetters.includes(letter);

    return (
      <Link key={ index } to={'/select/' + letter}>
        <div className={ available ? 'letter' : 'letter gray' }>
          <div className="letter-content">
            <div className="letter-absolute">{ letter.toUpperCase() }</div>
          </div>
        </div>
      </Link>
    );
  });
}

function getPlayerIcons(availablePlayers: Array<Player>, select: (player: Player) => void, selectedLetter?: string) {
  const players = !selectedLetter ? availablePlayers :
    availablePlayers.filter(player => player.displayName[0].toLowerCase() === selectedLetter);

  return players.map((player, index) => {
      return (
        <PlayerIcon key={ index } img={ player.imageUrl } name={ player.displayName } click={ () => select(player) } />
      );
    });
}

function PlayerSelection({ unavailableLetters, availablePlayers,
  selectPlayerFor, updateAvailablePlayers, select, match }: PlayerSelectionProps) {

  if (availablePlayers.length === 0) {
    // no player avaiable -> try to load them from backend
    PlayerService.getAllPlayers().then(updateAvailablePlayers);
  }

  let selectedLetter = '';
  if (match && match.params) {
    selectedLetter = match.params.letter;
  }

  const selectPlayer = (player: Player) => select(selectPlayerFor.team, selectPlayerFor.player, player);

  return (
    <div className={ 'player-selection' }>
      { !selectedLetter ?
        getLetters(unavailableLetters) :
        getPlayerIcons(availablePlayers, selectPlayer, selectedLetter ) }
    </div>
  );
}

export function mapStateToProps({ ranked: { availablePlayers, selectPlayerFor } }: any) {
  let unavailableLetters = alphabet;
  availablePlayers.forEach((player: Player) => {
    unavailableLetters = unavailableLetters.replace(player.displayName[0].toLowerCase(), '');
  });

  return {
    unavailableLetters,
    availablePlayers,
    selectPlayerFor
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
  return {
    updateAvailablePlayers: (players: Array<Player>) => dispatch(Actions.updateAvailablePlayers(players)),
    select: (team: TeamKey, player: PlayerKey, selected: Player) => {
      dispatch(Actions.setPlayer(team, player, selected));
      dispatch(push('/'));
    }
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(PlayerSelection);
