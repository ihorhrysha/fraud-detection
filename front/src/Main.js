import React from 'react';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';

import store from './store';
import App from './App';

const Main = props => (
  <BrowserRouter>
    <Provider store={store}>
      <App {...props} />
    </Provider>
  </BrowserRouter>
);

Main.displayName = 'Main';

export default Main;
