import { createAction } from 'redux-actions';

const updateUserDetails = createAction(
  'UPDATE_USER_DETAILS',
  data => data,
  undefined
);

export default updateUserDetails;
