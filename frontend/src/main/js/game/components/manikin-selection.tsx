import './manikin-selection.css';

import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import { match } from 'react-router';
import { push } from 'react-router-redux';

import { TeamColor } from '../../types/types';
import * as Actions from '../actions';
import { TimerService } from '../services/timer.service';
import { PartialStoreState } from '../store.state';

export interface ManikinSelectionProps {
    match?: match<any>;
    select: (team: TeamColor, player: string, manikin: string, time: number) => void;
}

interface ManikinSelectionState {
    manikins: Array<Array<string>>;
    team: TeamColor;
    player: string;
    position: string;
    goalTime: number;
}

export class ManikinSelectionComponent extends React.Component<ManikinSelectionProps, ManikinSelectionState> {
    constructor(props: ManikinSelectionProps) {
        super(props);

        let team = null;
        let position = null;
        let player = ''; // TODO: extend route parameters
        if (props.match && props.match.params) {
            team = props.match.params.team;
            position = props.match.params.position;
        }

        const teamPrefix = team === 'red' ? 'r' : 'b';
        const manikins = position === 'defense' ?
            this.createDefenseRows(teamPrefix) : this.createAttackRows(teamPrefix);
        this.state = { manikins, team, player, position, goalTime: TimerService.getTimeInSec() };
    }

    private createManikinsData(prefix: string, rows: Array<number>): Array<Array<string>> {
        return rows.map((rowCount, rowIndex) => {
            const manikinRow = [];
            for (let i = 1; i <= rowCount; i++) {
                manikinRow.push(`${ prefix }${ rowIndex + 1 }${ i }`);
            }
            return manikinRow;
        });
    }

    private createDefenseRows(prefix: string) {
        return this.createManikinsData(prefix, [ 1, 2 ]);
    }

    private createAttackRows(prefix: string) {
        return this.createManikinsData(prefix, [ 5, 3 ]);
    }

    private selectManikin(manikin: string): void {
        // tslint:disable-next-line:no-console
        console.log('Selecting ' + manikin);
        this.props.select(this.state.team, this.state.player, manikin, this.state.goalTime);
    }

    private renderManikinRow(row: Array<string>) {
        return row.map((manikin, index) => {
            return (
                <div
                    key={ index }
                    className={ `manikin ${ this.state.team }` }
                    onClick={ () => this.selectManikin(manikin) }
                >
                    { manikin }
                </div>
            );
        });
    }

    private renderManikins() {
        return this.state.manikins.map((row, index) => {
            return (
                <div key={ index } className="row">
                    { this.renderManikinRow(row) }
                </div>
            );
        });
    }

    public render() {
        if (!this.state.team || !this.state.position) {
            return null;
        }

        return (
            <div className="manikins">
                { this.renderManikins() }
            </div>
        );
    }
}

export function mapStateToProps(store: PartialStoreState) {
    return {};
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
    return {
        select: (team: TeamColor, player: string, manikin: string, time: number) => {
            dispatch(Actions.incGoals(team, player, manikin, time));
            dispatch(push('/'));
        }
    } as ManikinSelectionProps;
}

export default connect(mapStateToProps, mapDispatchToProps)(ManikinSelectionComponent);