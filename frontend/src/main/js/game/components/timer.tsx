import * as React from 'react';
import { connect, Dispatch } from 'react-redux';

import { Config } from '../../config';
import * as Actions from '../actions';
import { TimerService } from '../services/timer.service';
import { PartialStoreState } from '../store.state';

export type TimerProps = TimerPropValues & TimerPropActions;

interface TimerPropValues {
  startTime?: number;
  countdown?: boolean;
  draw: boolean;
}

interface TimerPropActions {
  expired?: () => void;
  start: (currentTime: number) => void;
  pause: (currentTime: number) => void;
}

interface TimerState {
  time: number;
  countdownTime: number;
  intervalId: any;
  status: 'STOPPED' | 'STARTED' | 'PAUSED';
  countdownExpired: boolean;
}

export class TimerComponent extends React.Component<TimerProps, TimerState> {
  private readonly interval = 500;

  constructor(props: TimerProps) {
    super(props);

    const id = setInterval(() => this.tick(), this.interval);
    
    this.state = {
      time: TimerService.getTimeInSec(),
      countdownTime: (this.props.startTime || 0) - TimerService.getCountdownTimeInSec(),
      intervalId: id,
      status: TimerService.getStatus(),
      countdownExpired: false
    };
  }

  private stopCountdownIfExpired(): void {
    if (this.props.countdown && !this.state.countdownExpired && this.state.countdownTime === 0) {
      this.setState({ countdownExpired: true });

      if (typeof this.props.expired === 'function') {
        // the timeout let the action be fired in the next tick
        // needed to ensure that the setState call is executed correctly
        setTimeout(() => this.props.expired!!());
      }
    }
  }

  private togglePause(): void {
    this.state.status === 'STARTED' ? this.props.pause(this.state.time) : this.props.start(this.state.time);
  }

  private tick() {
    const nextStatus = TimerService.getStatus();
    
    if (nextStatus === 'STOPPED') {
      if (this.state.status !== 'STOPPED' ) {
        this.setState({
          time: -1,
          countdownTime: this.props.startTime || 0,
          status: 'STOPPED',
          countdownExpired: false
        });
      }
    } else {
      const timerTime = TimerService.getTimeInSec();
      const countdownTime = TimerService.getCountdownTimeInSec();
      const countdownReset = countdownTime === 0;

      this.setState({
        time: timerTime,
        countdownTime: (this.props.startTime || 0) - countdownTime,
        countdownExpired: !countdownReset && this.state.countdownExpired,
        status: nextStatus
      });
    }
  }

  private formatTime(): string {
    const time = this.props.countdown ? this.state.countdownTime : this.state.time;
    const minutes = Math.floor(time / 60);
    const seconds = time % 60;

    return (minutes < 10 ? '0' : '') + minutes + ' : ' + (seconds < 10 ? '0' : '') + seconds;
  }

  private getStoppedText(): string {
    let text = 'START';
    if (this.props.countdown && this.state.countdownExpired) {
      text = this.props.draw ? 'SUDDEN DEATH' : 'ENDE';
    }
    return text;
  }

  public componentWillUnmount(): void {
    if (this.state.intervalId) {
      clearInterval(this.state.intervalId);
    }
  }

  public render() {
    this.stopCountdownIfExpired();

    const countdownReset = this.state.status === 'PAUSED' && this.state.countdownTime === this.props.startTime;
    const stopped = this.state.status === 'STOPPED' ||
                    this.props.countdown && (this.state.countdownExpired || countdownReset);

    return (
      <div className="timer">
        <span onClick={(e) => this.togglePause()}>
          { stopped ? this.getStoppedText() : this.formatTime() }
        </span>
      </div>
    );
  }
}

export function mapStateToProps({ ranked: { sets } }: PartialStoreState) {
  const currentSet = sets[sets.length - 1];
  const props: TimerPropValues = { draw: currentSet.blue.goals.length === currentSet.red.goals.length };
  
  if (Config.timedMatchMode) {
    props.countdown = true;
    props.startTime = Config.timePerSet;
  }

  return props;
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
  const actions = {
    start: (currentTime: number) => dispatch(Actions.startTimer(currentTime)),
    pause: (currentTime: number) => dispatch(Actions.pauseTimer(currentTime))
  } as TimerPropActions;
  
  if (Config.timedMatchMode) {
    actions.expired = () => dispatch(Actions.countdownExpired());
  }

  return actions;
}

export default connect(mapStateToProps, mapDispatchToProps)(TimerComponent);