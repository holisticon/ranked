import './panel.css';

import * as React from 'react';
import { connect, Dispatch } from 'react-redux';

import * as Actions from '../actions';
import { TimerService } from '../services/timer.service';
import { PartialStoreState } from '../store.state';
import TimerComponent from './timer';

export interface PanelProps {
  matchStarted: boolean;
}

export function Panel({ matchStarted }: PanelProps) {
  return (
    <div className={ 'panel' + (matchStarted ? ' closed' : '') }>
      <div className="features" />
      <div className="edge" />
      <div className="display">
        <div className="display-container">
          <TimerComponent />
        </div>
      </div>
    </div>
  );
}

export function mapStateToProps({ ranked: { } }: PartialStoreState): PanelProps {
  return {
    matchStarted: TimerService.getStatus() === 'STARTED'
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
  return {};
}

export default connect(mapStateToProps, mapDispatchToProps)(Panel);
