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
    changeable: boolean;
    leftIsUp: boolean;
    changeGoals: (increase: boolean) => void;
}

export interface GoalCounterProps {
    color: TeamColor;
    changeable?: boolean;
}

function GoalCounter({ value, flip, changeable, leftIsUp, changeGoals }: InternalGoalCounterProps) {
    return (
        <div className={ 'goal-counter-container' + (flip ? ' flip' : '') }>
            <div className="goal-counter">
                {
                    !changeable ? null :
                    <span className="material-icons arrow" onClick={ () => changeGoals(leftIsUp) }>
                        arrow_left
                    </span>
                }
                <span className="current-goals">{ value }</span>
                {
                    !changeable ? null :
                    <span className="material-icons arrow" onClick={ () => changeGoals(!leftIsUp) }>
                        arrow_right
                    </span>
                }
            </div>
        </div>
    );
}

export function mapStateToProps({ ranked: store }: PartialStoreState, { color, changeable }: GoalCounterProps) {
    const currentSet: Set = store.sets[store.sets.length - 1];
    const composition: Composition = currentSet[color];

    return {
        value: composition.goals.length,
        flip: color === 'red',
        changeable,
        leftIsUp: color === 'red'
    } as InternalGoalCounterProps;
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>, { color }: GoalCounterProps) {
    return {
        changeGoals: (increase: boolean) => dispatch(Actions.changeGoals(color, increase ? 1 : -1)),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(GoalCounter);