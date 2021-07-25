import React from 'react';
import ReactDOM from 'react-dom';

import { ThemeProvider } from '@material-ui/core';
import { IntlProvider } from 'react-intl'

import { CMSEditor, API, messages } from 'zoe-ide';
import siteTheme from './themes/siteTheme'

const locale = "en";
const service = API.mock();



declare global {
  interface Window {
    portalconfig?: {},
  }
}

const { portalconfig } = window;
const init = {
};

ReactDOM.render(
  <IntlProvider locale={locale} messages={messages[locale]}>
    <ThemeProvider theme={siteTheme}>
      <CMSEditor service={service} />
    </ThemeProvider>
  </IntlProvider>
  ,
  document.getElementById('root')
);