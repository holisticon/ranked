import { TimerComponent } from '../components/timer';

export class Timer {
  private static instance: Timer;

  public static get Service(): Timer {
    if (this.instance == null) {
      this.instance = new Timer();
    }
    return this.instance;
  }

  private timerComponent: TimerComponent;

  private constructor() { }

  public register(component: TimerComponent): void {
    this.timerComponent = component;
  }

  public start(): void {
    this.timerComponent.start();
  }

  public pause(): void {
    this.timerComponent.pause();
  }

  public reset(): void {
    this.timerComponent.reset();
  }

  public getTimeInSec(): number {
    return this.timerComponent == null ? 0 : this.timerComponent.getTime();
  }
}