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
    let webSocket: WebSocketService;
    let initStore: Store<any>;

    function createWebSocket(): void {
        webSocket = WebSocketService.new('middleware', Config.backendUrl + '/ranked');

        webSocket.open().subscribe(
            () => { return; },
            _ => {
                // tslint:disable-next-line:no-console
                console.log('Cannot connect to backend!');
                initStore.dispatch(Actions.interrupt(false));
            }
        );
        webSocket.listenTo<Event>('/topic/event').subscribe(event => {
            // ignore own events
            if (event.sender !== senderId) {
                initStore.dispatch(event);
            }
        });
    }

    export function create(): Middleware {
        return store => next => action => {
            if (action.type === Actions.RESET) {
                reset();
            }

            if (!(action as Event).sender && actionsForSync.indexOf(action.type) >= 0) {
                const event = { sender: senderId };
                Object.assign(event, action);
                webSocket.send('/event', event);
            }

            return next(action);
        };
    }

    export function init(store: Store<any>): void {
        initStore = store;
        createWebSocket();
    }

    export function reset(): void {
        if (!initStore) {
            throw new Error('You have to call WebSocketMiddleware.init before calling WebSocketMiddleware.reset!');
        }

        if (webSocket && webSocket.isOpen()) {
            webSocket.close();
        }

        createWebSocket();
    }
}