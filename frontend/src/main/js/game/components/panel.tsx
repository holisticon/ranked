import './panel.css';

import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import { Observable } from 'rxjs';

import * as Actions from '../actions';
import { TimerService } from '../services/timer.service';
import { WebSocketMiddleware } from '../services/websocket.middleware';
import { PartialStoreState, RankedStore } from '../store.state';
import GoalCounter from './goal-counter';
import { NoConnectionPanel } from './no-connection-panel';
import TimerComponent from './timer';

declare type PanelMode = 'ADMIN' | 'NO_CONNECTION';

export interface PanelProps {
  storeState: RankedStore;
  mode: PanelMode;
  panelClosed: boolean;
  collapse: () => void;
  reset: () => void;
  sync: (storeState: RankedStore) => void;
  reconnect: () => Observable<boolean>;
}

function renderAdminPanel({ storeState, reset, sync }: PanelProps): any {
  return (
    <div className="admin-panel">
      <div className="side-red">
        <GoalCounter color="red" changeable={ true } />
        <div className="buttons">
          <div className="namedButton" onClick={ () => reset() }>
            <div className="material-icons">power_settings_new</div>
            <div className="text">Reset</div>
          </div>
        </div>
      </div>
      <div className="devider" />
      <div className="side-blue">
        <GoalCounter color="blue" changeable={ true } />
        <div className="buttons">
          <div className="namedButton" onClick={ () => sync(storeState) }>
            <div className="material-icons">sync</div>
            <div className="text">Sync</div>
          </div>
        </div>
      </div>
    </div>
  );
}

function renderNoConnectionPanel({ reconnect }: PanelProps): any {
  return (
    <NoConnectionPanel reconnect={ () => reconnect() } />
  );
}

function renderFeaturesContent(props: PanelProps): any {
  switch (props.mode) {
    case 'NO_CONNECTION':
      return renderNoConnectionPanel(props);
    case 'ADMIN':
      return renderAdminPanel(props);
    default:
      return null;
  }
}

export function Panel(props: PanelProps) {
  return (
    <div className={ 'panel' + (props.panelClosed ? ' closed' : '') }>
      <div className="background" onClick={ () => props.collapse() } />
      <div className="features-container">
        <div className="features">
          { renderFeaturesContent(props) }
        </div>
      </div>
      <div className="edge" />
      <div className="display">
        <div className="display-container">
          <TimerComponent />
        </div>
      </div>
    </div>
  );
}

export function mapStateToProps({ ranked }: PartialStoreState) {
  let mode: PanelMode = 'ADMIN';
  if (TimerService.getStatus() === 'INTERRUPTED') {
    mode = 'NO_CONNECTION';
  }

  return {
    storeState: ranked,
    mode,
    panelClosed: TimerService.getStatus() !== 'INTERRUPTED' && (TimerService.getStatus() === 'STARTED' || TimerService.getTimeInSec() === 0)
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
  return {
    collapse: () => {
      dispatch(Actions.startTimer(TimerService.getTimeInSec()));
    },
    reset: () => dispatch(Actions.reset()),
    sync: (storeState: RankedStore) => dispatch(Actions.loadState(storeState)),
    reconnect: () => {
      return WebSocketMiddleware.reset()
        .map(() => {
          dispatch(Actions.interrupt(true));
          return true;
        })
        .catch(_ => Observable.of(false));
    }
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Panel);
