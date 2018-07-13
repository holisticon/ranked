import * as React from 'react';
import './heading.css';
import { Heading } from '../services/heading.service';

export interface HeadingConfig {
  title: string;
  iconPath: string;
  showBackButton?: boolean;
}

export class HeadingComponent extends React.Component<HeadingConfig, HeadingConfig> {

  constructor(props: HeadingConfig) {
    super(props);
    this.state = { ...props };

    Heading.Service.register(this);
  }

  public update(newState: HeadingConfig): void {
    this.setState(newState);
  }

  public render() {
    return (
      <div className="header">
          <div className="background" />
          {
            this.state.showBackButton ?
            <div className="back-button material-icons" onClick={  () => window.history.back() }>arrow_back_ios</div> :
            null
          }
          <div className="icon" style={{ backgroundImage: `url(${this.state.iconPath})` }} />
          <div className="title">{this.state.title}</div>
      </div>
    );
  }
}
