import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Route } from 'react-router-dom';
import { routerReducer, routerMiddleware, ConnectedRouter } from 'react-router-redux';
import { createStore, combineReducers, applyMiddleware } from 'redux';
import Match from './pages/match';
import PlayerSelection from './pages/player-selection';
import registerServiceWorker from '../../registerServiceWorker';
import createHistory from 'history/createBrowserHistory';
import { Provider } from 'react-redux';
import { ranked } from './reducer';
import './app.css';
import { Switch } from 'react-router';

// Create a history of your choosing (we're using a browser history in this case)
const history = createHistory();

// Build the middleware for intercepting and dispatching navigation actions
const middleware = routerMiddleware(history);

// Add the reducer to your store on the `router` key
// Also apply our middleware for navigating
const store = createStore(
  combineReducers({
    ranked,
    router: routerReducer
  }),
  applyMiddleware(middleware)
);

class Ranked extends React.Component {
  constructor(props: any) {
    super(props);
  }
  
  render() {
    return (
      <div className="ranked">
        <ConnectedRouter history={history}>
          <Switch>
            <Route exact={true} path="/" component={ Match } />
            <Route path="/select/:letter?" component={ PlayerSelection } />
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
