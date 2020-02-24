// eslint-disable-next-line no-unused-vars
import React, { useState, useEffect } from 'react';
// import { useDispatch } from 'react-redux';
import { Button } from 'antd';
import 'antd/dist/antd.css';
import faker from 'faker';

import { Usercard } from '../components';
// import { fetchComments } from '../actions';
import '../css/style.css';

const axios = require('axios');

export const MainPage = () => {
  // eslint-disable-next-line no-unused-vars
  let timedGenerator;
  // const dispatch = useDispatch();

  // eslint-disable-next-line no-unused-vars
  const [fakeData, setFakeData] = useState([]);
  const [blackList, setBlackList] = useState([
    'item1',
    'item2',
    'item3',
    'item4'
  ]);
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
      const ip = faker.internet.ip();
      fakeArray.push({ id, name, email, ip });
    }
    let clonedStateArray = JSON.parse(JSON.stringify(fakeData));
    clonedStateArray = clonedStateArray.concat(fakeArray);
    setFakeData(clonedStateArray);
  };

  return (
    <div className="app">
      <h2>Parallel Functional and Streaming Programming. Final Project.</h2>
      <h3>
        Team: Ihor Hrysha, Iaroslav Plutenko, Natalia Rudenko, Andrii Blagodyr{' '}
      </h3>
      <div className="container">
        <Button
          className="sender"
          style={{ marginLeft: 8 }}
          onClick={generateFakeUsers}
        >
          Send Data
        </Button>
        <div className="generated_data">
          {fakeData.length > 0
            ? fakeData.map(item => <Usercard key={item.id} {...item} />)
            : null}
        </div>
        <div className="errors">
          <p>Black Listed Items</p>
          {blackList.length > 0
            ? blackList.map(item => (
                <div
                  key={item}
                  style={{
                    color: 'white',
                    backgroundColor: 'indigo',
                    borderRadius: '5px',
                    padding: '7px',
                    margin: '5px',
                    width: '100%',
                    boxShadow: '15px 17px 17px -12px rgba(75,0,130,1)'
                  }}
                >
                  {item}
                </div>
              ))
            : null}
        </div>
      </div>
    </div>
  );
};

MainPage.displayName = 'MainPage';
