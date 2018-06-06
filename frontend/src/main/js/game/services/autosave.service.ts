import { Middleware, Action } from 'redux';
import { RankedStore, PartialStoreState } from '../store.state';
import { COUNTDOWN_EXPIRED } from '../actions';

export namespace AutosaveService {
  let lastState: RankedStore;
  let lastAction: Action;

  export function autosaveMiddleware(): Middleware {
    return store => next => action => {
      lastAction = action;
      lastState = (store.getState() as any as PartialStoreState).ranked;
      return next(action);
    };
  }

  export function getLastState(): RankedStore {
    if (getLastAction().type === COUNTDOWN_EXPIRED) {
      lastState = { ...lastState, suddenDeathMode: true };
    }
    return lastState;
  }

  export function getLastAction(): Action {
    return lastAction;
  }
}