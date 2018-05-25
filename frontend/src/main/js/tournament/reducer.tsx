import * as Actions from './actions';
import { TournamentStoreState, defaultState } from './store.state';

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
      }));

    default:
      break;
  }

  return state || defaultState();
}
