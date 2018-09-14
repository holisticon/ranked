import './manikin-selection.css';

import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import { match } from 'react-router';
import { push } from 'react-router-redux';

import { Config } from '../../config';
import { PlayerService } from '../../services/player-service';
import { Player, TeamColor } from '../../types/types';
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
    player?: Player;
    position: string;
    goalTime: number;
    timerPercent: number;
}

export class ManikinSelectionComponent extends React.Component<ManikinSelectionProps, ManikinSelectionState> {
    private timer: any;

    constructor(props: ManikinSelectionProps) {
        super(props);

        let team = null;
        let position = null;
        if (props.match && props.match.params) {
            team = props.match.params.team;
            position = props.match.params.position;

            let playerId = props.match.params.player;
            if (playerId) {
                PlayerService.getPlayer(playerId).then(player => this.setState({ player }));
            }
        }

        const teamPrefix = team === 'red' ? 'r' : 'b';
        const manikins = position === 'defense' ?
            this.createDefenseRows(teamPrefix) : this.createAttackRows(teamPrefix);
        this.state = { manikins, team, position, goalTime: TimerService.getTimeInSec(), timerPercent: 100 };

        this.startTimer();
    }

    private startTimer(): void {
        const decrease = 0.1;
        this.timer = setInterval(
            () => {
                let timerPercent = this.state.timerPercent - decrease;
                if (timerPercent <= 0) {
                    timerPercent = 0;
                }
                this.setState({ timerPercent });
            },
            Config.timeForManikinSelection * 10 * decrease
        );
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
        if (this.timer) {
            // always clear the interval before routing back
            clearInterval(this.timer);
        }

        this.props.select(this.state.team, this.state.player ? this.state.player.id : '', manikin, this.state.goalTime);
    }

    private renderManikinRow(row: Array<string>) {
        return row.map((manikin, index) => {
            return (
                <div
                    key={ index }
                    className={ `manikin ${ this.state.team }` }
                    onClick={ () => this.selectManikin(manikin) }
                >
                    <div className="manikin-inner">
                        <span className="manikin-icon" />
                    </div>
                </div>
            );
        });
    }

    private renderManikins() {
        return this.state.manikins.map((row, index) => {
            return (
                <div key={ index } className="column">
                    { this.renderManikinRow(row) }
                </div>
            );
        });
    }

   private renderOwnGoalButton() {
      return (
          <div className="own-goal-button ranked-button ranked-button-gray"
               onClick={ () => this.props.select(this.state.team, '', '', this.state.goalTime) }>
            <span>Eigentor</span>
          </div>
    );
  }

    public render() {
        if (!this.state.team || !this.state.position) {
            return null;
        }

        if (this.state.timerPercent === 0) {
            // let the render update completely before redirect
            setTimeout(() => this.selectManikin(''));
        }

        return (
            <div className={ 'manikin-selection' + ( this.state.team === 'red' ? ' rotate-180' : '' ) }>
                <div className="manikins">
                    { this.renderManikins() }
                </div>
                { this.renderOwnGoalButton() }
                <div className={`timer ${ this.state.team }`}>
                    <div className="timer-bar" style={ { width: this.state.timerPercent + '%' } } />
                </div>
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
