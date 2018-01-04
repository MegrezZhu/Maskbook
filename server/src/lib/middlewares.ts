import busboy = require('koa-busboy');
import { IMiddleware, IRouterContext } from 'koa-router';
import uuid = require('uuid/v4');
import { avatarImageDir, isDev, postImageDir } from '../config';
import { User } from '../model/entity/User';
import { repo } from '../model/index';
import { assertError } from './assert';
import ErrorCode from './ErrorCode';
import logger from './logger';

function genFilename (ext = '') {
  return `${uuid()}${ext ? '.' : ''}${ext}`;
}

export interface ILoggedInContext extends IRouterContext {
  user: User; // add not null infomation
}

type ILoginGuardedMiddleware = (ctx: ILoggedInContext, next: () => Promise<any>) => Promise<any>;

export function loginGuard (fn: ILoginGuardedMiddleware): IMiddleware {
  return async (ctx: IRouterContext, next: () => Promise<any>) => {
    if (!ctx.user) {
      assertError('not logged in', ErrorCode.Login_Required);
    } else {
      await fn(ctx as ILoggedInContext, next);
    }
  };
}

export async function errorHandler (ctx: IRouterContext, next: () => Promise<any>) {
  try {
    await next();
  } catch (err) {
    if (err.expose) {
      ctx.status = 400;
      ctx.body = {
        errno: err.errno,
        message: err.message || 'Unknown Error'
      };
    } else {
      ctx.status = 500;
      if (isDev) {
        ctx.body = err.stack;
      }
      logger.error(err);
    }
  }
}

export async function autoLogin (ctx: IRouterContext, next: () => Promise<any>) {
  if (ctx.session.uid) {
    // if not found, treat as not-logged-in.
    ctx.user = await repo.user.findOneById(ctx.session.uid) || null;
  } else {
    ctx.user = null;
  }
  await next();
}

export const postImageUploader = busboy({
  dest: postImageDir,
  fnDestFilename: genFilename.bind(null, 'jpg')
});

export const avatarImageUploader = busboy({
  dest: avatarImageDir,
  fnDestFilename: genFilename.bind(null, 'jpg')
});
