export type TimerStatus = 'STOPPED' | 'STARTED' | 'PAUSED' | 'INTERRUPTED';

export namespace TimerService {
  let intervalId: any;
  let status: TimerStatus = 'STOPPED';
  let interruptedStatus: TimerStatus;
  let timeInSec: number = 0;
  let countdownTimeInSec: number = 0;

  function clear(): void {
    if (intervalId) {
      clearInterval(intervalId);
      intervalId = null;
    }
  }

  export function setTime(time: number): void {
    if (status !== 'STARTED') {
      timeInSec = time;
    }
  }

  export function start(): void {
    if (!intervalId) {
      intervalId = setInterval(
        () => {
          timeInSec++;
          countdownTimeInSec++;
        },
        1000);
      status = 'STARTED';
    }
  }

  export function pause(): void {
    status = 'PAUSED';
    clear();
  }

  export function reset(): void {
    clear();
    timeInSec = 0;
    status = 'STOPPED';
  }

  export function interrupt(): void {
    if (status !== 'INTERRUPTED') {
      interruptedStatus = status;
      status = 'INTERRUPTED';
      clear();
    }
  }

  export function moveOn(): void {
    if (status === 'INTERRUPTED') {
      status = interruptedStatus;
      if (interruptedStatus === 'STARTED') {
        start();
      }
    }
  }

  export function resetCountdown(countdownTime?: number): void {
    countdownTimeInSec = countdownTime || 0;
  }

  export function getStatus(): TimerStatus {
    return status;
  }

  export function getTimeInSec(): number {
    return Math.max(timeInSec, 0);
  }

  export function getCountdownTimeInSec(): number {
    return Math.max(countdownTimeInSec, 0);
  }
}