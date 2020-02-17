import { handleActions } from 'redux-actions';

export const initialUserDetailsState = {};

export const userDetails = handleActions(
  {
    FETCH_USER_DETAILS_FULFILLED: (state, { payload: { data } }) => data
  },
  initialUserDetailsState
);
