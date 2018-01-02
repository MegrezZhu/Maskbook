import 'reflect-metadata';
import 'source-map-support';

import * as Koa from 'koa';
import bodyparser = require('koa-bodyparser');
import morgan = require('koa-morgan');
import koaSession = require('koa-session');

import { isDev, session } from './config';
import logger from './lib/logger';
import { repo } from './model/index';
import router from './router';

(async () => {
  await repo.init(); // init connection with database

  const app = new Koa();

  if (isDev) {
    app.use(morgan('dev'));
  }

  app.keys = session.cookieKey;
  app
    .use(koaSession(session, app))
    .use(bodyparser())
    .use(router.routes());

  app.listen(process.env.PORT || 7001);

  app.on('error', err => {
    if (!err.expose) {
      logger.error(err);
    }
  });
})();

process.on('unhandledRejection', (reason) => logger.error(reason));
