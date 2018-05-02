import { TimerComponent } from '../components/timer';

export namespace TimerService {
  let timerComponent: TimerComponent;

  export function register(component: TimerComponent): void {
    timerComponent = component;
  }

  export function start(): void {
    timerComponent.start();
  }

  export function pause(): void {
    timerComponent.pause();
  }

  export function reset(): void {
    timerComponent.reset();
  }

  export function resetCountdown(): void {
    timerComponent.resetCountdown();
  }

  export function getTimeInSec(): number {
    return timerComponent == null ? 0 : timerComponent.getTime();
  }
}