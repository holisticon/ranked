import './panel.css';

import * as React from 'react';
import { connect, Dispatch } from 'react-redux';

import * as Actions from '../actions';
import { TimerService } from '../services/timer.service';
import { PartialStoreState } from '../store.state';
import GoalCounter from './goal-counter';
import TimerComponent from './timer';

export interface PanelProps {
  panelClosed: boolean;
  collapse: () => void;
}

export function Panel({ panelClosed, collapse }: PanelProps) {
  return (
    <div className={ 'panel' + (panelClosed ? ' closed' : '') }>
      <div className="background" onClick={ () => collapse() } />
      <div className="features-container">
        <div className="features">
          <div className="side-red">
            <GoalCounter color="red" />
          </div>
          <div className="side-blue">
            <GoalCounter color="blue" />
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

export function mapStateToProps({ ranked: { } }: PartialStoreState) {
  return {
    panelClosed: TimerService.getStatus() === 'STARTED' || TimerService.getTimeInSec() === 0
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
  return {
    collapse: () => {
      dispatch(Actions.startTimer(TimerService.getTimeInSec()));
    }
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Panel);
