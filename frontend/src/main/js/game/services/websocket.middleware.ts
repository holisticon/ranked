import { Store } from 'react-redux';
import { Middleware } from 'redux';

import { Config } from '../../config';
import * as Actions from '../actions';
import { WebSocketService } from './websocket.service';

type Event = Actions.RankedAction & {
    sender?: string;
};

export namespace WebSocketMiddleware {
    const actionsForSync = [
        Actions.INC_GOALS,
        Actions.CHANGE_GOALS,
        Actions.SET_PLAYER, 
        Actions.SWITCH_PLAYER_POSITION, 
        Actions.START_NEW_MATCH, 
        Actions.RESUME_MATCH, 
        Actions.PAUSE_MATCH,
        Actions.LOAD_STATE
    ];

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
        client = WebSocketService.new('middleware', Config.backendUrl + '/ranked');

        client.open().subscribe(
            () => { return; },
            _ => alert('Cannot connect to backend!')
        );
        client.listenTo<Event>('/topic/event').subscribe(event => {
            // ignore own events
            if (event.sender !== senderId) {
                store.dispatch(event);
            }
        });
    }
}