import './dialog.css';

import * as React from 'react';

export type ButtonType = 'ok' | 'error' | 'warn' | 'gray';

export interface DialogProps {
  class?: string;
  headline: string;
  text: string;
  buttons: Array<{
    text: string,
    type: ButtonType,
    click: () => void
  }>;
}

export class Dialog extends React.Component<DialogProps> {
  constructor(props: DialogProps) {
    super(props);
  }

  getButtons() {
    return this.props.buttons.map((btn, index) => {
      return (
        <div className={ 'ranked-button ranked-button-' + btn.type } key={ index } onClick={ btn.click }>
          { btn.text }
        </div>
      );
    });
  }

  render() {
    return (
      <div className={ 'dialog-container ' + (this.props.class || '') }>
        <div className="dialog-content">
          <div className="dialog-headline">{ this.props.headline }</div>
          <div className="dialog-text">{ this.props.text }</div>
          <div className="dialog-button-container">
            { this.getButtons() }
          </div>
        </div>
      </div>
    );
  }
}
