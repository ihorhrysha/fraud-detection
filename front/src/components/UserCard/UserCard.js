import React from 'react';
import { List, Avatar } from 'antd';

export const Usercard = ({ name, email, ip }) => (
  <List.Item
    style={{
      width: '360px',
      marginBottom: '5px',
      lineHeight: '0',
      boxShadow: '15px 17px 15px -17px rgba(0,0,0,0.75)'
    }}
  >
    <List.Item.Meta
      avatar={
        <>
          <Avatar
            src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png"
            style={{ paddingLeft: '10px' }}
          />
        </>
      }
      title={name}
      description={
        <>
          <pre>Mail: {email}</pre>
          <pre>IP: {ip}</pre>
        </>
      }
    />
  </List.Item>
);

Usercard.displayname = 'Usercard';
