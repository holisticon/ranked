import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { Player } from '../../types/types';
import { match as Match } from 'react-router';
import { push } from 'react-router-redux';
import { PartialStoreState } from '../store.state';
import { PlayerSelectionComponent } from '../../components/player-selection';
import { PlayerService } from '../../services/player-service';

export interface PlayerSelectionProps {
  match?: Match<any>;
  availablePlayers: Array<Player>;
  alreadySelectedPlayers: Array<Player>;
  select: (selected: Player) => void;
  updateAvailablePlayers: (players: Array<Player>) => void;
}

function tournamentPlayerSelection({
  availablePlayers, alreadySelectedPlayers, select, updateAvailablePlayers, match }: PlayerSelectionProps) {

  if (availablePlayers.length === 0) {
    // no player avaiable -> try to load them from backend
    PlayerService.getAllPlayers().then(updateAvailablePlayers);
  }

  let selectedLetter;
  if (match && match.params) {
    selectedLetter = match.params.letter;
  }

  const selectPlayer = (player: Player) => select(player);

  return (
    <PlayerSelectionComponent
      urlPrefix="/tournamentAdmin"
      selectedLetter={ selectedLetter }
      availablePlayers={ availablePlayers }
      markedPlayerIds={ alreadySelectedPlayers.map(player => player.id) }
      select={ (player) => selectPlayer(player) }
    />
  );
}

export function mapStateToProps(
  { tournament: { tournamentParticipants, availablePlayers } }: PartialStoreState) {
  
  return {
    availablePlayers,
    alreadySelectedPlayers: tournamentParticipants
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.TournamentAction>) {
  return {
    updateAvailablePlayers: (players: Array<Player>) => dispatch(Actions.updatePlayers(players)),
    select: (player: Player) => {
      dispatch(Actions.addParticipant(player));
      dispatch(push('/tournamentAdmin'));
    }
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(tournamentPlayerSelection);
