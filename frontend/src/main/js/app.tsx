import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Route } from 'react-router-dom';
import { routerReducer, routerMiddleware, ConnectedRouter } from 'react-router-redux';
import { createStore, combineReducers, applyMiddleware } from 'redux';
import Match from './game/pages/match';
import PlayerSelection from './game/pages/player-selection';
import TeamSelection from './game/pages/team-selection';
import MatchSelection from './game/pages/match-selection';
import registerServiceWorker from './registerServiceWorker';
import createHistory from 'history/createBrowserHistory';
import { Provider } from 'react-redux';
import { ranked } from './game/reducer';
import './app.css';
import { Switch } from 'react-router';
import { ScoreBoard } from './statistics/pages/player-score-board';
import { Config } from './config';
import { TournamentTree } from './tournament/pages/tournament-tree';
import { TeamScoreBoard } from './statistics/pages/team-score-board';
import { Seacon } from './seacon';
import TournamentAdminPage from './tournament/pages/tournament-administration';
import { tournament } from './tournament/reducer';
import TournamentPlayerSelection from './tournament/pages/tournament-player-selection';

// Create a history of your choosing (we're using a browser history in this case)
const history = createHistory();

// Build the middleware for intercepting and dispatching navigation actions
const middleware = routerMiddleware(history);

// Add the reducer to your store on the `router` key
// Also apply our middleware for navigating
const store = createStore(
  combineReducers({
    ranked,
    tournament,
    router: routerReducer
  }),
  applyMiddleware(middleware)
);

class Ranked extends React.Component<{}, { initialized: boolean }> {
  constructor(props: any) {
    super(props);
    this.state = { initialized: false };
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
        <ConnectedRouter history={history}>
          <Switch>
            <Route exact={ true } path="/" component={ Match } />
            <Route path="/select/:letter?" component={ PlayerSelection } />
            <Route path="/selectTeam" component={ TeamSelection } />
            <Route path="/board" component={ ScoreBoard } />
            <Route path="/tournament" component={ TournamentTree } />
            <Route exact={ true } path="/tournamentAdmin" component={ TournamentAdminPage } />
            <Route path="/tournamentAdmin/select/:letter?" component={ TournamentPlayerSelection } />
            <Route path="/selectMatch" component={ MatchSelection } />
            <Route path="/teamBoard" component={ TeamScoreBoard } />
            <Route path="/seacon" component={ Seacon } />
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
