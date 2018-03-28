import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { PartialStoreState } from '../store.state';
import './panel.css';
import { TimerComponent } from './timer';

export interface PanelProps {
}

function Panel({ }: PanelProps) {
  return (
    <div className="panel">
      <div className="display">
        <TimerComponent />
      </div>
    </div>
  );
}

export function mapStateToProps({ ranked: { } }: PartialStoreState) {
  return {};
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
  return {};
}

export default connect(mapStateToProps, mapDispatchToProps)(Panel);
