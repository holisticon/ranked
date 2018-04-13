import * as React from 'react';
import { ChartData2D } from '../types';
import { Player } from '../../types/types';
import { PlayerIcon } from '../../components/player-icon';
import './ranking-chart.css';

type DoubleBarChartProps = {
  data?: ChartData2D<Player, number | string>
};

export class DoubleBarChart extends React.Component<DoubleBarChartProps, any> {
  constructor(props: any) {
    super(props);
  }

  public render() {
    if (!this.props || !this.props.data || this.props.data.entries.length === 0) {
      return null;
    }
    
    return null;
  }
}
