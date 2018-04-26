import * as React from 'react';
import { TimerService } from '../services/timer.service';
import { PartialStoreState } from '../store.state';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { Config } from '../../config';

export interface TimerProps {
  startTime?: number;
  countdown?: boolean;
  expired?: () => void;
  draw?: boolean;
}

interface TimerState {
  interval: number;
  time: number;
  intervalId: any;
  status: 'STOPPED' | 'STARTED' | 'PAUSED';
  isExpired: boolean;
}

export class TimerComponent extends React.Component<TimerProps, TimerState> {

  constructor(props: TimerProps) {
    super(props);
    this.state = {
      interval: props.countdown ? -1 : 1,
      time: props.startTime || -1,
      intervalId: null,
      status: 'STOPPED',
      isExpired: false
    };

    TimerService.register(this);
  }

  public start(): void {
    if (this.state.status !== 'STARTED') {
      const id = setInterval(() => this.tick(), Math.abs(this.state.interval) * 1000);
      this.setState({ time: Math.max(0, this.state.time), intervalId: id, status: 'STARTED' });
    }
  }

  public reset(): void {
    if (this.state.intervalId) {
      clearInterval(this.state.intervalId);
    }
    this.setState({ time: this.props.startTime || -1, intervalId: null, status: 'STOPPED', isExpired: false });
  }

  public pause(): void {
    if (this.state.status === 'STARTED') {
      if (this.state.intervalId) {
        clearInterval(this.state.intervalId);
      }
      this.setState({ intervalId: null, status: 'PAUSED' });
    }
  }

  private stopCountdownIfExpired(): void {
    if (this.state.status !== 'STOPPED' && this.props.countdown && this.state.time === 0) {
      if (this.state.intervalId) {
        clearInterval(this.state.intervalId);
      }
      this.setState({ intervalId: null, status: 'STOPPED', isExpired: true });

      if (typeof this.props.expired === 'function') {
        // the timeout let the action be fired in the next tick
        // needed to ensure that the setState call is executed correctly
        setTimeout(() => this.props.expired!!());
      }
    }
  }

  private togglePause(): void {
    this.state.status === 'STARTED' ? this.pause() : this.start();
  }

  private tick() {
    this.setState({ time: this.state.time + this.state.interval });
  }

  private formatTime(): string {
    const minutes = Math.floor(this.state.time / 60);
    const seconds = this.state.time % 60;

    return (minutes < 10 ? '0' : '') + minutes + ' : ' + (seconds < 10 ? '0' : '') + seconds;
  }

  public getTime(): number {
    return Math.max(this.state.time, 0);
  }

  private getStoppedText(): string {
    let text = 'START';
    if (this.props.countdown && this.state.isExpired) {
      text = this.props.draw ? 'SUDDEN DEATH' : 'ENDE';
    }
    return text;
  }

  public render() {
    this.stopCountdownIfExpired();

    return (
      <div className="timer">
        <span onClick={(e) => this.togglePause()}>
          {this.state.status === 'STOPPED' ? this.getStoppedText() : this.formatTime()}
        </span>
      </div>
    );
  }
}

export function mapStateToProps({ ranked: { sets } }: PartialStoreState) {
  const currentSet = sets[sets.length - 1];
  const props: TimerProps = { draw: currentSet.blue.goals.length === currentSet.red.goals.length };
  
  if (Config.timedMatchMode) {
    props.countdown = true;
    props.startTime = Config.timePerSet;
  }

  return props;
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
  if (Config.timedMatchMode) {
    return { expired: () => dispatch(Actions.countdownExpired()) } as TimerProps;
  }
  return {};
}

export default connect(mapStateToProps, mapDispatchToProps)(TimerComponent);