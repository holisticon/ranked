import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { createStore } from 'redux';
import Match from './pages/match';
import registerServiceWorker from '../../registerServiceWorker';
import { Provider } from 'react-redux';
import { StoreState, defaultState } from './types/store.state';
import { rankedReducer } from './reducer';
import './app.css';

class Ranked extends React.Component {
  constructor(props: any) {
    super(props);
  }

  render() {
    return (
      <div className="ranked">
        <Match />
      </div>
    );
  }
}

const store = createStore<StoreState>(rankedReducer, defaultState());

ReactDOM.render(
  <Provider store={store}>
    <Ranked />
  </Provider>,
  document.getElementById('root')
);
registerServiceWorker();
