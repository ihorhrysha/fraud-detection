import { createStore, applyMiddleware, compose, combineReducers } from 'redux';
import promise from 'redux-promise-middleware';
import thunkMiddleware from 'redux-thunk';
import { connectRouter, routerMiddleware } from 'connected-react-router';
// eslint-disable-next-line import/no-extraneous-dependencies
import { createBrowserHistory } from 'history';

import {
  userDetails,
  initialUserDetailsState,
  comments,
  initialCommentsState
} from '../reducers';

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

export const history = createBrowserHistory();

const createRootReducer = history =>
  combineReducers({
    userDetails: userDetails,
    comments: comments,
    router: connectRouter(history)
  });

const enhancer = composeEnhancers(
  applyMiddleware(thunkMiddleware, promise, routerMiddleware(history))
);

export const initialStoreState = {
  userDetails: initialUserDetailsState,
  comments: initialCommentsState
};

const store = createStore(
  createRootReducer(history),
  initialStoreState,
  enhancer
);

export default store;
