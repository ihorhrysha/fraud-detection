// eslint-disable-next-line no-unused-vars
import React, { useState, useEffect } from 'react';
// import { useDispatch } from 'react-redux';
import { Button } from 'antd';
import 'antd/dist/antd.css';
import faker from 'faker';

import { Usercard } from '../components';
// import { fetchComments } from '../actions';

export const MainPage = () => {
  // eslint-disable-next-line no-unused-vars
  let timedGenerator;
  // const dispatch = useDispatch();

  // eslint-disable-next-line no-unused-vars
  const [fakeData, setFakeData] = useState([]);

  // useEffect(() => {
  //   dispatch(fetchComments());
  // }, [fakeData]);

  const generateFakeUsers = () => {
    const fakeArray = [];
    // eslint-disable-next-line no-plusplus
    for (let index = 0; index < 5; index++) {
      const id = Math.floor(Math.random() * 100000);
      const name = faker.name.findName();
      const email = faker.internet.email();
      fakeArray.push({ id, name, email });
    }
    let clonedStateArray = JSON.parse(JSON.stringify(fakeData));
    clonedStateArray = clonedStateArray.concat(fakeArray);
    setFakeData(clonedStateArray);
  };

  return (
    <div className="App">
      <Button
        type="primary"
        style={{ marginLeft: 8 }}
        onClick={generateFakeUsers}
      >
        Start Generation
      </Button>
      {fakeData.length > 0
        ? fakeData.map(item => <Usercard key={item.id} {...item} />)
        : null}
    </div>
  );
};

MainPage.displayName = 'MainPage';
