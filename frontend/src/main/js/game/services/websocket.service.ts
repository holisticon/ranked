import * as StompJS from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';

export class WebSocketService {
    private static knownServices: { [key: string]: WebSocketService} = {};

    private client: StompJS.Client;
    private eventSubscriptions: Array<StompJS.StompSubscription>;

    private initializeSubject: BehaviorSubject<boolean>;

    public static new(id: string, url: string): WebSocketService {
        if (!this.knownServices[id]) {
            this.knownServices[id] = new WebSocketService(url);       
        }
        return this.knownServices[id];
    }

    public static get(id: string): WebSocketService {
        return this.knownServices[id];
    }

    private constructor(private url: string) {
        this.initializeSubject = new BehaviorSubject(false);
        this.eventSubscriptions = [];
    }

    private init(): void {
        this.initializeSubject = new BehaviorSubject(false);
        this.eventSubscriptions = [];
    }

    public open(): Observable<void> {
        return new Observable<void>(observer => {
            this.client = StompJS.client('ws://' + this.url);
            this.client.connect(
                {},
                () => {
                    this.initializeSubject.next(true);
                    observer.next();
                },
                error => observer.error(error)
            );
        });
    }

    public close(): void {
        this.eventSubscriptions.forEach(subscription => subscription.unsubscribe());

        this.initializeSubject.complete();
        this.init();
    }

    public send<T>(path: string, body: string | T): void {
        this.initializeSubject.subscribe(initialized => {
            if (initialized) {
                if (typeof body !== 'string') {
                    body = JSON.stringify(body);
                }
                this.client.send(path, {}, body);
            }
        });
    }

    public listenTo<T>(path: string): Observable<T> {
        return new Observable<T>(observer => {
            this.initializeSubject.subscribe(initialized => {
                if (initialized) {
                    this.eventSubscriptions.push(this.client.subscribe(path, message => {
                        observer.next(JSON.parse(message.body));
                    }));
                }
            });
        });
    }
}