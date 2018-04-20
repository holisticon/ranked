import * as React from 'react';
import { ChartData3D } from '../types';
import { Player } from '../../types/types';
import './double-bar-chart.css';
import { PlayerIcon } from '../../components/player-icon';

type DoubleBarChartProps = {
  data?: ChartData3D<Player, number, number>,
  cumulationHeadline?: string,
  cumulate?: (val1: number, val2: number) => number | string
};

export class DoubleBarChart extends React.Component<DoubleBarChartProps, any> {
  constructor(props: any) {
    super(props);
  }

  private calcPercentage(value: number, min: number, max: number): number {
    return (value - min) / (max - min);
  }

  private dim(index: number): string {
    return this.props.data!!.dimensions[index].unit || '';
  }

  public render() {
    if (!this.props || !this.props.data || this.props.data.entries.length === 0) {
      return null;
    }

    let minFirstValue = Math.min(...this.props.data.entries.map(entry => entry[1]));
    let maxFirstValue = Math.max(...this.props.data.entries.map(entry => entry[1]));
    let minSecondValue = Math.min(...this.props.data.entries.map(entry => entry[2]));
    let maxSecondValue = Math.max(...this.props.data.entries.map(entry => entry[2]));
    let minValue = Math.min(minFirstValue, minSecondValue);
    let maxValue = Math.max(maxFirstValue, maxSecondValue);

    return this.props.data.entries
      .map((entry, i) => {
        const firstBarWidth = this.calcPercentage(entry[1], minValue, maxValue) * 90 + 10;
        const secondBarWidth = this.calcPercentage(entry[2], minValue, maxValue) * 90 + 10;

        return (
          <div key="i" className="double-bar-entry">
            <div className="player-icon">
              <PlayerIcon img={ entry[0].imageUrl } click={() => { return; }} />
            </div>
            <div className="name">{ entry[0].displayName }</div>
            {
              this.props.cumulate ? 
              <div className="cumulation">{ this.props.cumulate(entry[1], entry[2]) }</div> :
              null
            }
            <div className="line">
              <div className="icon"><img src="/img/attack.png" /></div>
              <div className="bar">
                <div className="bar-inner" style={{ width: firstBarWidth + '%' }} />
                <div className="bar-value" style={{ marginLeft: firstBarWidth + '%' }}>{ entry[1] + this.dim(1) }</div>
              </div>
            </div>
            <div className="line">
              <div className="icon"><img src="/img/defense.png" /></div>
              <div className="bar">
                <div className="bar-inner" style={{ width: secondBarWidth + '%' }} />
                <div className="bar-value" style={{ marginLeft: secondBarWidth + '%' }}>{ entry[2] + this.dim(2) }</div>
              </div>
            </div>
          </div>
        );
      });
  }
}
