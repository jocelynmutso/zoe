import React from 'react';
import ReactDOM from 'react-dom';
import { ThemeProvider } from '@material-ui/core';

import siteTheme from './themes/siteTheme'

declare global {
  interface Window { 
    portalconfig?: {},
  }
}

const { portalconfig } = window;
const init = {
};

ReactDOM.render( 
  <ThemeProvider theme={siteTheme}>
    <div>hello world</div>
  </ThemeProvider>
  ,
  document.getElementById('root')
);