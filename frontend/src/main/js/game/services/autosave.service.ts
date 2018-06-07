import { Middleware, Action } from 'redux';
import { COUNTDOWN_EXPIRED } from '../actions';

export namespace AutosaveService {
  let lastState: { [stateKey: string]: any } = {};
  let lastAction: { [stateKey: string]: Action } = {};

  export function autosaveMiddleware(stateKey: string, saveToLocalStorage: boolean = false): Middleware {
    return store => next => action => {
      lastAction[stateKey] = action;
      lastState[stateKey] = store.getState()[stateKey];
      const result = next(action);

      if (saveToLocalStorage) {
        localStorage.setItem(stateKey, JSON.stringify(store.getState()[stateKey]));
      }

      return result;
    };
  }

  export function getLastState(stateKey: string): any {
    if (getLastAction(stateKey).type === COUNTDOWN_EXPIRED) {
      lastState = { ...lastState, suddenDeathMode: true };
    }
    return lastState;
  }

  export function getLastAction(stateKey: string): Action {
    return lastAction[stateKey];
  }

  export function loadStateFromLocalStorage(stateKey: string): any | null {
    const savedState = localStorage.getItem(stateKey);
    if (savedState) {
      try {
        return JSON.parse(savedState);
      } catch (e) {
        // just ignore it
      }
    }

    return null;
  }
}