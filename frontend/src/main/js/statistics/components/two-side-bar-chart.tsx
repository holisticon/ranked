import * as React from 'react';
import { ChartData3D } from '../types';
import { Player } from '../../types/types';
import './two-side-bar-chart.css';

type TwoSideBarChartProps = {
  data?: ChartData3D<Player, number, number>,
  cumulationHeadline?: string,
  cumulate?: (val1: number, val2: number) => number | string
};

export class TwoSideBarChart extends React.Component<TwoSideBarChartProps, any> {
  constructor(props: any) {
    super(props);
  }

  private calcPercentage(value: number, min: number, max: number): number {
    return (value - min) / (max - min);
  }

  public render() {
    if (!this.props || !this.props.data || this.props.data.entries.length === 0) {
      return null;
    }

    const minPosValue = Math.min(...this.props.data.entries.map(entry => entry[2]));
    const maxPosValue = Math.max(...this.props.data.entries.map(entry => entry[2]));
    const minNegValue = Math.min(...this.props.data.entries.map(entry => entry[1]));
    const maxNegValue = Math.max(...this.props.data.entries.map(entry => entry[1]));

    return this.props.data.entries
      .map((entry, i) => {
        const negBarWidth = this.calcPercentage(entry[1], minNegValue, maxNegValue) * 90 + 10;
        const posBarWidth = this.calcPercentage(entry[2], minPosValue, maxPosValue) * 90 + 10;

        return (
          <div key="i" className="two-side-bar-entry">
            <div className="name">{ entry[0].displayName }</div>
            <div className="negative-bar">
              <div className="bar-inner" style={{ width: negBarWidth + '%' }} />
              <div className="bar-value" style={{ paddingRight: negBarWidth + '%' }}>{ entry[1] }</div>
            </div>
            <div className="positive-bar">
              <div className="bar-inner" style={{ width: posBarWidth + '%' }} />
              <div className="bar-value" style={{ marginLeft: posBarWidth + '%' }}>{ entry[2] }</div>
            </div>
            {
              this.props.cumulate ? 
              <div className="cumulation">{ this.props.cumulate(entry[1], entry[2]) }</div> :
              null
            }
          </div>
        );
      });
  }
}
