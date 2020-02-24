import Axios from 'axios';

export async function sendUsers(array) {
  const response = await Axios.request({
    url: 'http://localhost:8082/topics/user-activity-data',
    method: 'post',
    data: {
      array
    },
    headers: {
      'Content-Type': 'application/vnd.kafka.avro.v2+json',
      Accept: 'application/vnd.kafka.v2+json'
    }
  });
  return { result: response.data };
}
