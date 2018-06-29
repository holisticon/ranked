import * as React from 'react';
import { ChartData2D } from '../types';
import './trend-chart.css';

const RV = require('react-vis');

type Point = {
    x: number | string | Date,
    y: number | string | Date
};

type TrendChartProps = {
    data: ChartData2D<Date, number>
};

type TrendChartState = {
    data: Array<Point>,
    firstLastPoint: [Point, Point]
};

export class TrendChart extends React.Component<TrendChartProps, TrendChartState> {
    constructor(props: any) {
        super(props);

        const chartData = this.createChartDataFrom(this.props.data.entries);
        this.state = { data: chartData, firstLastPoint: [chartData[0], chartData.slice(-1)[0]] };
    }

    private createChartDataFrom(raw: Array<[string | number | Date, string | number | Date]>): Array<any> {
        return raw.map(data => { return { x: data[0], y: data[1] }; });
    }

    private isToday(d: Date): boolean {
        const today = new Date();
        return today.getDate() === d.getDate() &&
            today.getMonth() === d.getMonth() &&
            today.getFullYear() === d.getFullYear();
    }

    private renderDate(date: Date): string {
        if (this.isToday(date)) {
            return 'Heute';
        } else {
            const options = { day: '2-digit', month: '2-digit', year: 'numeric' };
            return date.toLocaleDateString('de-DE', options);
        }
    }

    public render() {
        if (!this.props || !this.props.data || this.props.data.entries.length === 0) {
            return null;
        }

        return (
            <RV.XYPlot className="trend-chart" width={500} height={300} xType="time">
                <RV.LineMarkSeries data={this.state.data} size="0" color="#4b93e2" />
                <RV.YAxis
                    tickValues={this.state.firstLastPoint.slice(0, 1).map(p => p.y)}
                    tickFormat={(v: number) => `${v}`}
                    style={{ line: {stroke: 'transparent'} }}
                    tickSize={0}
                />
                <RV.YAxis
                    tickValues={this.state.firstLastPoint.slice(-1).map(p => p.y)}
                    tickFormat={(v: number) => `${v}`}
                    style={{ line: {stroke: 'transparent'} }}
                    orientation="right"
                    tickSize={0}
                />
                <RV.XAxis
                    tickValues={this.state.firstLastPoint.map(p => p.x)}
                    tickFormat={(d: Date) => this.renderDate(d)}
                    tickSize={0}
                    tickPadding={12}
                />
            </RV.XYPlot>
        );
    }
}
