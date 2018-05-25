import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { Player, PlayerKey, TeamKey } from '../../types/types';
import { match as Match } from 'react-router';
import { push } from 'react-router-redux';
import { PlayerService } from '../../services/player-service';
import { PartialStoreState } from '../store.state';
import { PlayerSelectionComponent } from '../../components/player-selection';

export interface PlayerSelectionProps {
  match?: Match<any>;
  availablePlayers: Array<Player>;
  selectFor: any;
  alreadySelectedPlayers: Array<Player>;
  select: (team: TeamKey, player: PlayerKey, selected: Player) => void;
  updateAvailablePlayers: (players: Array<Player>) => void;
}

function PlayerSelection({
  availablePlayers, selectFor, updateAvailablePlayers, alreadySelectedPlayers, select, match }: PlayerSelectionProps) {

  if (availablePlayers.length === 0) {
    // no player avaiable -> try to load them from backend
    PlayerService.getAllPlayers().then(updateAvailablePlayers);
  }

  let selectedLetter;
  if (match && match.params) {
    selectedLetter = match.params.letter;
  }

  const selectPlayer = (player: Player) => select(selectFor.team, selectFor.player, player);

  return (
    <div className={ 'player-selection' }>
      <PlayerSelectionComponent
        selectedLetter={ selectedLetter }
        availablePlayers={ availablePlayers }
        markedPlayerIds={ alreadySelectedPlayers.map(player => player.id) }
        select={ (player) => selectPlayer(player) }
      />
    </div>
  );
}

export function mapStateToProps({ ranked: { availablePlayers, selectFor, team1, team2 } }: PartialStoreState) {
  const teamPlayers = [ team1.player1, team1.player2, team2.player1, team2.player2 ];
  
  return {
    availablePlayers,
    selectFor,
    alreadySelectedPlayers: teamPlayers.filter(player => !!player.id)
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
