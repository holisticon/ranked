import * as React from 'react';
import { ChartData2D } from '../types';
import { Player, Team } from '../../types/types';
import { PlayerIcon } from '../../components/player-icon';
import './ranking-chart.css';

type RankingChartProps = {
  data?: ChartData2D<Player | Team | string, number | string>
};

export class RankingChart extends React.Component<RankingChartProps, any> {
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

    const first = +this.props.data.entries[0][1];
    const last = +this.props.data.entries[this.props.data.entries.length - 1][1];
    const minValue = Math.min(first, last);
    const maxValue = Math.max(first, last);

    return this.props.data.entries
      .map((entry, i) => {
        const barWidth = this.calcPercentage(+entry[1], minValue, maxValue) * 70 + 30;

        let imageUrl: string = '';
        let displayName: string;

        if (entry[0] instanceof Player) {
          imageUrl = (entry[0] as Player).imageUrl;
          displayName = (entry[0] as Player).displayName;
        } else if (entry[0] instanceof Team) {
          imageUrl = (entry[0] as Team).imageUrl;
          displayName = (entry[0] as Team).name || '';
        } else {
          displayName = (entry[0] as string);
        }

        return (
          <div key="i" className="ranking-entry">
            <div className="icon">
              <PlayerIcon img={ imageUrl } click={() => { return; }} />
            </div>
            <div className="name">{ displayName }</div>
            <div className="bar">
              <div className="bar-inner" style={{ width: barWidth + '%' }} />
            </div>
            <div className="score">{ entry[1] }{ this.props.data!!.dimensions[1].unit }</div>
          </div>
        );
      });
  }
}
