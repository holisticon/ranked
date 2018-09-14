import './app.css';
import 'react-vis/dist/style.css';

import createHistory from 'history/createBrowserHistory';
import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { Switch } from 'react-router';
import { Route } from 'react-router-dom';
import { ConnectedRouter, routerMiddleware, routerReducer } from 'react-router-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';

import { Config } from './config';
import ManikinSelection from './game/components/manikin-selection';
import DevicePositionSelection from './game/pages/device-position-selection';
import Match from './game/pages/match';
import MatchSelection from './game/pages/match-selection';
import PlayerSelection from './game/pages/player-selection';
import TeamSelection from './game/pages/team-selection';
import { ranked } from './game/reducer';
import { AutosaveService } from './game/services/autosave.service';
import { WebSocketMiddleware } from './game/services/websocket.middleware';
import registerServiceWorker from './registerServiceWorker';
import { ScoreBoard } from './statistics/pages/player-score-board';
import { Profile } from './statistics/pages/profile';
import { ProfileSelection } from './statistics/pages/profile-selection';
import { TeamScoreBoard } from './statistics/pages/team-score-board';
import TournamentAdminPage from './tournament/pages/tournament-administration';
import TournamentPlayerSelection from './tournament/pages/tournament-player-selection';
import { TournamentTree } from './tournament/pages/tournament-tree';
import { tournament } from './tournament/reducer';

// Create a history of your choosing (we're using a browser history in this case)
const history = createHistory();

// Build the middleware for intercepting and dispatching navigation actions
const middleware = routerMiddleware(history);

// build the middleware for autosaving our store states
const rankedAutosaveMiddleware = AutosaveService.autosaveMiddleware('ranked');
const tournamentAutosaveMiddleware = AutosaveService.autosaveMiddleware('tournament', true);

// build the middleware for websocket connection and synchronize
const webSocketMiddleware = WebSocketMiddleware.create();

// Add the reducer to your store on the `router` key
// Also apply our middleware for navigating
const store = createStore(
  combineReducers({
    ranked,
    tournament,
    router: routerReducer
  }),
  applyMiddleware(middleware, rankedAutosaveMiddleware, tournamentAutosaveMiddleware, webSocketMiddleware)
);

WebSocketMiddleware.init(store);

class Ranked extends React.Component<{}, { initialized: boolean }> {
  constructor(props: any) {
    super(props);
    this.state = { initialized: true };
  }

  componentWillMount(): void {
    Config.initConfig().then(() => this.setState({ initialized: true }));
  }

  render() {
    if (!this.state.initialized) {
      return null;
    }

    return (
      <div className="ranked">
        <ConnectedRouter history={ history }>
          <Switch>
            <Route exact={ true } path="/" component={ Match } />
            <Route path="/devicePosition" component={ DevicePositionSelection } />
            <Route path="/select/:letter?" component={ PlayerSelection } />
            <Route path="/selectTeam" component={ TeamSelection } />
            <Route path="/selectManikin/:team/:position/:player?" component={ ManikinSelection } />
            <Route path="/board" component={ ScoreBoard } />
            <Route path="/tournament" component={ TournamentTree } />
            <Route exact={ true } path="/tournamentAdmin" component={ TournamentAdminPage } />
            <Route path="/tournamentAdmin/select/:letter?" component={ TournamentPlayerSelection } />
            <Route path="/selectMatch" component={ MatchSelection } />
            <Route path="/teamBoard" component={ TeamScoreBoard } />
            <Route path="/profile/player/:playerName" component={ Profile } />
            <Route path="/profile" component={ ProfileSelection } />
          </Switch>
        </ConnectedRouter>
      </div>
    );
  }
}

ReactDOM.render(
  <Provider store={store}>
    <Ranked />
  </Provider>,
  document.getElementById('root')
);

registerServiceWorker();
