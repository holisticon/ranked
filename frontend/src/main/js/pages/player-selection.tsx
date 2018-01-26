import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { Player, TeamColor, PlayerKey } from '../types/types';
import axios from 'axios';
import { PlayerIcon } from '../components/player_icon';
import { Link } from 'react-router-dom';
import { match as Match } from 'react-router';
import { push } from 'react-router-redux';
import './player-selection.css';

const alphabet = 'abcdefghijklmnopqrstuvwxyz';

export interface PlayerSelectionProps {
  match?: Match<any>;
  upsideDown: boolean;
  unavailableLetters: string;
  availablePlayers: Array<Player>;
  selectPlayerFor: any;
  select: (team: TeamColor, position: PlayerKey, player: Player) => void;
  updateAvailablePlayers: (players: Array<Player>) => void;
}

function getPlayers(): Promise<Array<Player>> {
  // TODO: use real backend
  return axios.get('/players.json').then(res => res.data);
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
    availablePlayers.filter(player => player.name[0].toLowerCase() === selectedLetter);

  return players.map((player, index) => {
      return (
        <PlayerIcon key={ index } img={ player.img } click={ () => select(player) } />
      );
    });
}

function PlayerSelection({ upsideDown, unavailableLetters, availablePlayers,
  selectPlayerFor, updateAvailablePlayers, select, match }: PlayerSelectionProps) {

  if (availablePlayers.length === 0) {
    // no player avaiable -> try to load them from backend
    getPlayers().then(updateAvailablePlayers);
  }

  let selectedLetter = '';
  if (match && match.params) {
    selectedLetter = match.params.letter;
  }

  const selectPlayer = (player: Player) => select(selectPlayerFor.team, selectPlayerFor.position, player);

  const rotation = upsideDown ? 'upside-down' : '';
  const classes = `${ rotation } player-selection`;

  return (
    <div className={ classes }>
      { !selectedLetter ?
        getLetters(unavailableLetters) :
        getPlayerIcons(availablePlayers, selectPlayer, selectedLetter ) }
    </div>
  );
}

export function mapStateToProps({ ranked: { availablePlayers, selectPlayerFor } }: any) {
  let unavailableLetters = alphabet;
  availablePlayers.forEach((player: Player) => {
    unavailableLetters = unavailableLetters.replace(player.name[0].toLowerCase(), '');
  });

  return {
    upsideDown: selectPlayerFor && selectPlayerFor.team === 'red',
    unavailableLetters,
    availablePlayers,
    selectPlayerFor
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
  return {
    updateAvailablePlayers: (players: Array<Player>) => dispatch(Actions.updateAvailablePlayers(players)),
    select: (team: TeamColor, position: PlayerKey, player: Player) => {
      dispatch(Actions.setPlayer(team, position, player));
      dispatch(push('/'));
    }
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(PlayerSelection);
