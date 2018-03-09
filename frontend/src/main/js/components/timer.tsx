import * as React from 'react';
import { Timer } from '../misc/timer.service';

export interface TimerProps {
}

interface TimerState {
  time: number;
  intervalId: any;
  status: 'STOPPED' | 'STARTED' | 'PAUSED';
}

export class TimerComponent extends React.Component<TimerProps, TimerState> {
  private readonly interval = 1;

  constructor(props: TimerProps) {
    super(props);
    this.state = { time: 0, intervalId: null, status: 'STOPPED' };

    Timer.Service.register(this);
  }

  public start(): void {
    const id = setInterval(() => this.tick(), this.interval * 1000);
    this.setState({ time: this.state.time, intervalId: id, status: 'STARTED' });
  }

  public reset(): void {
    if (this.state.intervalId) {
      clearInterval(this.state.intervalId);
    }
    this.setState({ time: 0, intervalId: null, status: 'STOPPED' });
  }

  public pause(): void {
    if (this.state.intervalId) {
      clearInterval(this.state.intervalId);
    }
    this.setState({ time: this.state.time, intervalId: null, status: 'PAUSED' });
  }

  private togglePause(): void {
    this.state.status === 'STARTED' ? this.pause() : this.start();
  }

  private tick() {
    this.setState({ time: this.state.time + this.interval });
  }

  private formatTime(): string {
    const minutes = Math.floor(this.state.time / 60);
    const seconds = this.state.time % 60;

    return (minutes < 10 ? '0' : '') + minutes + ' : ' + (seconds < 10 ? '0' : '') + seconds;
  }

  public getTime(): number {
    return this.state.time;
  }

  public render() {
    return (
      <div className="timer">
        <span onClick={(e) => this.togglePause()}>
          {this.state.status === 'STOPPED' ? 'START' : this.formatTime()}
        </span>
      </div>
    );
  }
}