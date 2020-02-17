import React from 'react';
import { List, Avatar } from 'antd';

// eslint-disable-next-line react/display-name
// eslint-disable-next-line react/prop-types
export const Usercard = ({ name, email }) => (
  <List.Item style={{ width: '50%', margin: 'auto' }}>
    <List.Item.Meta
      avatar={
        <Avatar src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png" />
      }
      title={name}
      description={email}
    />
    <div>Content</div>
  </List.Item>
);

Usercard.displayname = 'Usercard';
