import 'reflect-metadata';
import 'source-map-support/register';

import * as fs from 'fs-extra';
import * as Koa from 'koa';
import bodyparser = require('koa-bodyparser');
import morgan = require('koa-morgan');
import mount = require('koa-mount');
// import { IRouterContext } from 'koa-router';
import koaSession = require('koa-session');
import koaStatic = require('koa-static');

import { avatarImageDir, isDev, postImageDir, publicDir, session } from './config';
import logger from './lib/logger';
import { repo } from './model/index';
import router from './router';

import './@types/TypePatch';
import { autoLogin, errorHandler } from './lib/middlewares';

(async () => {
  await Promise.all([
    repo.init(), // init connection with database
    fs.ensureDir(postImageDir),
    fs.ensureDir(avatarImageDir)
  ]);

  logger.info('successfully connected to database.');

  const app = new Koa();

  if (isDev) {
    app.use(morgan('dev'));
  }

  app.keys = session.cookieKey;
  app
    .use(mount('/public', koaStatic(publicDir)))
    .use(koaSession(session, app));

  // if (isDev) {
  //   // mock login state
  //   app.use(async (ctx: IRouterContext, next: () => Promise<any>) => {
  //     ctx.session.uid = 16;
  //     await next();
  //   });
  // }

  app
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
