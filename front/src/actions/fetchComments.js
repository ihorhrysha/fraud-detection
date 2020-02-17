import axios from 'axios';
import { createAction } from 'redux-actions';

const payloadCreator = () =>
  axios.get('https://jsonplaceholder.typicode.com/posts/1/comments');

const fetchCommentsAsync = createAction('FETCH_COMMENTS', payloadCreator);

const fetchComments = () => dispatch => dispatch(fetchCommentsAsync());

fetchComments.toString = fetchCommentsAsync.toString;

export default fetchComments;
