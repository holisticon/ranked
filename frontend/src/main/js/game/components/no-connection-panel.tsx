import './no-connection-panel.css';

import * as React from 'react';
import { Observable } from 'rxjs';

export interface NoConnectionPanelProps {
    reconnect: () => Observable<boolean>;
}

export interface NoConnectionPanelState {
    timerId: any;
    reconnectInterval: number;
    timerTime: number;
}

export class NoConnectionPanel extends React.Component<NoConnectionPanelProps, NoConnectionPanelState> {

    constructor(props: NoConnectionPanelProps) {
        super(props);

        this.state = this.buildState(10);
    }

    private buildState(reconnectInterval: number): NoConnectionPanelState {
        const timerId = setInterval(
            () => {
                this.tick();
            },
            1000
        );

        return { timerId, timerTime: reconnectInterval, reconnectInterval };
    }

    private reconnect(): void {
        clearInterval(this.state.timerId);

        const subscibtion = this.props.reconnect().subscribe(success => {
            subscibtion.unsubscribe();
            if (!success) {
                this.setState(this.buildState(this.state.reconnectInterval * 2));
            }
        });
    }

    private tick(): void {
        const newTimerTime = this.state.timerTime - 1;

        if (newTimerTime <= 0) {
            this.reconnect();
        }

        this.setState({ timerTime: newTimerTime });
    }

    private tryToReconnect(): void {
        this.reconnect();
        this.setState({ timerTime: 0 });
    }

    private formatTime(): string {
        const time = this.state.timerTime;
        const minutes = Math.floor(time / 60);
        const seconds = time % 60;

        return (minutes < 10 ? '0' : '') + minutes + ':' + (seconds < 10 ? '0' : '') + seconds;
    }

    public render() {
        const reconnecting = this.state.timerTime <= 0;
        return (
            <div className="no-connection-panel">
                <div className="heading">Keine Verbindung</div>
                <div className="description">
                    { reconnecting ? 'Verbindungsversuch ...' : `Nächster Versuch in ${ this.formatTime() } ...` }
                </div>
                { reconnecting ? null : <div className="text-button" onClick={ () => this.tryToReconnect() }>Jetzt verbinden</div> }
            </div>
        );
    }

}