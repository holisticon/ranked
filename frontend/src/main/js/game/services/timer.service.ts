export namespace TimerService {
  let intervalId: any;
  let status: 'STOPPED' | 'STARTED' | 'PAUSED' = 'STOPPED';
  let timeInSec: number = 0;
  let countdownTimeInSec: number = 0;

  function clear(): void {
    if (intervalId) {
      clearInterval(intervalId);
      intervalId = null;
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

  export function resetCountdown(countdownTime?: number): void {
    countdownTimeInSec = countdownTime || 0;
  }

  export function getStatus(): 'STOPPED' | 'STARTED' | 'PAUSED' {
    return status;
  }

  export function getTimeInSec(): number {
    return Math.max(timeInSec, 0);
  }

  export function getCountdownTimeInSec(): number {
    return Math.max(countdownTimeInSec, 0);
  }
}