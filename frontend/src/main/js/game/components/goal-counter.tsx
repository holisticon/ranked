import './goal-counter.css';

import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';

import { Composition, Set, TeamColor } from '../../types/types';
import * as Actions from '../actions';
import { PartialStoreState } from '../store.state';


interface InternalGoalCounterProps {
    value: number;
    flip: boolean;
}

export interface GoalCounterProps {
    color: TeamColor;
}

function GoalCounter({ value, flip }: InternalGoalCounterProps) {
    return (
        <div className={ 'goal-counter-container' + (flip ? ' flip' : '') }>
            <div className="goal-counter">
                <span className="current-goals">{ value }</span>
            </div>
        </div>
    );
}

export function mapStateToProps({ ranked: store }: PartialStoreState, { color }: GoalCounterProps) {
    const currentSet: Set = store.sets[store.sets.length - 1];
    const composition: Composition = currentSet[color];

    return {
        value: composition.goals.length,
        flip: color === 'red'
    };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
    return {};
}

export default connect(mapStateToProps, mapDispatchToProps)(GoalCounter);