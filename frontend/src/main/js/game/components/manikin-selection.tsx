import './manikin-selection.css';

import * as React from 'react';

import { TeamColor } from '../../types/types';

export interface ManikinSelectionProps {
    team: TeamColor;
    playerPosition: string;
    select: (manikin: string) => void;
    abort: () => void;
}

interface ManikinSelectionState {
    manikins: Array<Array<string>>;
}

export class ManikinSelectionComponent extends React.Component<ManikinSelectionProps, ManikinSelectionState> {
    constructor(props: ManikinSelectionProps) {
        super(props);

        const teamPrefix = props.team === 'red' ? 'r' : 'b';
        const manikins = props.playerPosition === 'defense' ? 
            this.createDefenseRows(teamPrefix) : this.createAttackRows(teamPrefix);
        this.state = { manikins };
    }

    private createManikinsData(prefix: string, rows: Array<number>): Array<Array<string>> {
        return rows.map((rowCount, rowIndex) => {
            const manikinRow = [];
            for (let i = 1; i <= rowCount; i++) {
                manikinRow.push(`${prefix}${rowIndex + 1}${i}`);
            }
            return manikinRow;
        });
    }

    private createDefenseRows(prefix: string) {
        return this.createManikinsData(prefix, [1, 2]);
    }

    private createAttackRows(prefix: string) {
        return this.createManikinsData(prefix, [5, 3]);
    }

    private renderManikinRow(row: Array<string>) {
        return row.map((manikin, index) => {
            return (
                <div key={ index } className={ `manikin ${this.props.team}` }>
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
        return (
            <div className="manikins">
                { this.renderManikins() }
            </div>
        );
    }
}