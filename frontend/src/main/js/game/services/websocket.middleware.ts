import { Store } from 'react-redux';
import { Middleware } from 'redux';

import { DEC_GOALS, INC_GOALS, RankedAction, SET_PLAYER, START_NEW_MATCH, SWITCH_PLAYER_POSITION } from '../actions';
import { WebSocketService } from './websocket.service';

type Event = RankedAction & {
    sender?: string;
};

export namespace WebSocketMiddleware {
    const actionsForSync = [ INC_GOALS, DEC_GOALS, SET_PLAYER, SWITCH_PLAYER_POSITION, START_NEW_MATCH ];
    const senderId = '' + Math.ceil(Math.random() * 1000);
    let client: WebSocketService;

    export function create(): Middleware {
        return store => next => action => {
            if (!(action as Event).sender && actionsForSync.indexOf(action.type) >= 0) {
                const event = { sender: senderId };
                Object.assign(event, action);
                client.send('/event', event);
            }

            return next(action);
          };
    }

    export function init<S>(store: Store<S>): void {
        client = WebSocketService.new('middleware', '10.5.13.124:8086/ranked');

        client.open().subscribe(
            () => { return; },
            _ => alert('Cannot connect to backend!')
        );
        client.listenTo<Event>('/topic/event').subscribe(event => {
            // tslint:disable-next-line:no-console
            console.log(`Receive some ${event.type} event from '${event.sender}'`);
            // ignore own events
            if (event.sender !== senderId) {
                store.dispatch(event);
            }
        });
    }
}