import './panel.css';

import * as React from 'react';
import { connect, Dispatch } from 'react-redux';

import * as Actions from '../actions';
import { TimerService } from '../services/timer.service';
import { PartialStoreState, RankedStore } from '../store.state';
import GoalCounter from './goal-counter';
import TimerComponent from './timer';

export interface PanelProps {
  storeState: RankedStore;
  panelClosed: boolean;
  collapse: () => void;
  reset: () => void;
  sync: (storeState: RankedStore) => void;
}

export function Panel({ storeState, panelClosed, collapse, reset, sync }: PanelProps) {
  return (
    <div className={ 'panel' + (panelClosed ? ' closed' : '') }>
      <div className="background" onClick={ () => collapse() } />
      <div className="features-container">
        <div className="features">
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
  return {
    storeState: ranked,
    panelClosed: TimerService.getStatus() === 'STARTED' || TimerService.getTimeInSec() === 0
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
  return {
    collapse: () => {
      dispatch(Actions.startTimer(TimerService.getTimeInSec()));
    },
    reset: () => dispatch(Actions.reset()),
    sync: (storeState: RankedStore) => dispatch(Actions.loadState(storeState))
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Panel);
