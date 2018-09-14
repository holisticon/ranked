import './device-position-selection.css';

import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import { push } from 'react-router-redux';

import { TeamColor } from '../../types/types';
import * as Actions from '../actions';

export interface DevicePositionSelectionProps {
    select: (position: TeamColor | null) => void;
}

function DevicePositionSelection({ select }: DevicePositionSelectionProps) {
    return (
        <div className="device-position-selection">
            <div className="ranked-button position-blue" onClick={ () => select('blue') }>
                Blaues Tor
            </div>
            
            <div className="ranked-button position-red" onClick={ () => select('red') }>
                Rotes Tor
            </div>
            
            <div className="ranked-button single-mode" onClick={ () => select(null) }>
                Nur ein Smartphone
            </div>
        </div>
    );
}

export function mapStateToProps() {
    return { };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>): DevicePositionSelectionProps {
    return {
        select: (position: TeamColor | null) => {
            dispatch(Actions.setDevicePosition(position));
            dispatch(push('/'));
        }
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(DevicePositionSelection);
