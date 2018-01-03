import 'reflect-metadata';
import 'source-map-support/register';

import * as Koa from 'koa';
import bodyparser = require('koa-bodyparser');
import morgan = require('koa-morgan');
import koaSession = require('koa-session');

import { isDev, session } from './config';
import logger from './lib/logger';
import { repo } from './model/index';
import router from './router';

import './@types/TypePatch';
import { autoLogin, errorHandler } from './lib/middlewares';

(async () => {
  await repo.init(); // init connection with database

  logger.info('successfully connected to database.');

  const app = new Koa();

  if (isDev) {
    app.use(morgan('dev'));
  }

  app.keys = session.cookieKey;
  app
    .use(koaSession(session, app))
    .use(bodyparser())
    .use(errorHandler)
    .use(autoLogin)
    .use(router.routes());

  const port = process.env.PORT || 7001;
  app.listen(port, () => {
    logger.info(`Server listening at ${port}.`);
  });
})();

process.on('unhandledRejection', (reason) => logger.error(reason));
