import * as React from 'react';
import { ChartData2D } from '../types';
import { Player } from '../../types/types';
import { PlayerIcon } from '../../components/player-icon';
import './ranking-chart.css';

type RankingChartProps = {
  data?: ChartData2D<Player, number>
};

export class RankingChart extends React.Component<RankingChartProps, any> {
  constructor(props: any) {
    super(props);
  }

  private calcPercentage(value: number, min: number, max: number): number {
    return (value - min) / (max - min);
  }

  public render() {
    if (!this.props || !this.props.data) {
      return null;
    }

    const maxValue = this.props.data.entries[0][1];
    const minValue = this.props.data.entries[this.props.data.entries.length - 1][1];

    return this.props.data.entries
      .map((entry, i) => {
        const barWidth = this.calcPercentage(entry[1], minValue, maxValue) * 70 + 30;

        return (
          <div key="i" className="ranking-entry">
            <div className="icon">
              <PlayerIcon img={entry[0].imageUrl} click={() => { return; }} />
            </div>
            <div className="name">{entry[0].displayName}</div>
            <div className="bar">
              <div className="bar-inner" style={{ width: barWidth + '%' }} />
            </div>
            <div className="score">{entry[1]}</div>
          </div>
        );
      });
  }
}
