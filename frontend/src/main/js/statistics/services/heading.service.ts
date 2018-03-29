import { HeadingComponent, HeadingConfig } from '../components/heading';

export class Heading {
  private static instance: Heading;

  public static get Service(): Heading {
    if (Heading.instance == null) {
      Heading.instance = new Heading();
    }
    return Heading.instance;
  }

  private headingComponent: HeadingComponent;

  private constructor() { }

  public register(component: HeadingComponent): void {
    this.headingComponent = component;
  }

  public update(config: HeadingConfig): void {
    this.headingComponent.update(config);
  }
}