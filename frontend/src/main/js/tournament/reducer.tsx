import * as Actions from './actions';
import { TournamentStoreState, defaultState } from './store.state';

// tslint:disable-next-line:max-line-length
const names = ['ferhatayaz', 'carstensahling', 'danielsteinhoefer', 'danielwegener', 'dennisholtfreter', 'christianneuenstadt', 'christophwolff', 'christophgerkens', 'lukastaake', 'leonfausten', 'jangalinski', 'joehm', 'jochenmeyer', 'nilsernsting', 'michaelfritsch', 'maltesoerensen', 'oliverniebsch', 'oliverihns', 'robinpommerenke', 'romanschloemmer', 'patrickschalk', 'simonnehls', 'simonspruenker', 'simonzambrovski', 'stefanheldt', 'stefanmerkl', 'stefanzilske', 'thorstenrahlf', 'timogroeger', 'tobiasbehr', 'tobiasstamann', 'wiebkedahl'];

function copyAndSet<T>(item: any, setter: (itemCopy: T) => void): T {
  const copy: T = {...item};
  setter(copy);

  return copy;
}

export function tournament(
  state: TournamentStoreState,
  tournamentAction: Actions.TournamentAction
): TournamentStoreState {
  switch (tournamentAction.type) {
    case Actions.ADD_PARTICIPANT:
      let addAction = tournamentAction as Actions.AddParticipant;
      if (state.tournamentParticipants.some(player => player.id === addAction.player.id)) {
        // player was already selected
        return state;
      }

      return copyAndSet(state, ((copyState: TournamentStoreState)  => {
        copyState.tournamentParticipants.push(addAction.player);
      }));

    case Actions.REMOVE_PARTICIPANT:
      let removeAction = tournamentAction as Actions.RemoveParticipant;
      return copyAndSet(state, ((copyState: TournamentStoreState)  => {
        copyState.tournamentParticipants = copyState.tournamentParticipants
          .filter((item => item.id !== removeAction.player.id));
      }));

    case Actions.UPDATE_PLAYERS:
      let updatePlayerAction = tournamentAction as Actions.UpdatePlayers;
      return copyAndSet(state, ((copyState: TournamentStoreState)  => {
        copyState.availablePlayers = updatePlayerAction.players;
        // TODO: REMOVE, JUST FOR TESTING
        copyState.tournamentParticipants = names.map(name => updatePlayerAction.players.find(p => p.id === name)!);
      }));

    default:
      break;
  }

  return state || defaultState();
}
