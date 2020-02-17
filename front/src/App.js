import React from 'react';
import './App.css';
import { Route, Switch } from 'react-router-dom';
import { ConnectedRouter } from 'connected-react-router';

import { MainPage } from './pages';
import { history } from './store';

const App = () => (
  <ConnectedRouter history={history}>
    <Switch>
      <Route path="/" component={MainPage} />
    </Switch>
  </ConnectedRouter>
);

App.displayName = 'App';

export default App;
