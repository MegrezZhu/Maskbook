import { validate } from 'class-validator';
import { IRouterContext } from 'koa-router';
import { omit, pick } from 'lodash';
import { assert, assertError } from '../lib/assert';
import ErrorCode from '../lib/ErrorCode';
import { ILoggedInContext } from '../lib/middlewares';
import { stringify } from '../lib/stringifyValidateError';
import { IUser, User } from '../model/entity/User';
import { userService } from '../service/index';

export async function regist (ctx: IRouterContext) {
  const user = User.fromInterface((ctx.request.body || {}) as IUser);

  const [validateErr] = await validate(user);
  if (validateErr) {
    assertError(stringify(validateErr), ErrorCode.Invalid_Arguments);
  }

  await userService.regist(user);
  ctx.body = pick(user, ['id', 'username', 'nickname', 'avatar', 'power']);
}

export async function login (ctx: IRouterContext) {
  const { username, password }: { username: string, password: string } = ctx.request.body;
  assert(username && typeof username === 'string', 'invalid username', ErrorCode.Invalid_Arguments);
  assert(password && typeof password === 'string', 'invalid password', ErrorCode.Invalid_Arguments);

  const user = await userService.login(username, password);
  if (!user) {
    assertError('incorrect username or password', ErrorCode.Login_Failed);
  } else {
    ctx.session.uid = user.id;
    ctx.body = omit(user, ['password']);
  }
}

// not caring about if one is actually logged in
export async function logout (ctx: IRouterContext) {
  ctx.session = null;
  ctx.status = 200;
}

export async function edit (ctx: ILoggedInContext) {
  const { nickname, password }: { nickname: string, password: string } = ctx.request.body;

  ctx.user.nickname = nickname || ctx.user.nickname;
  ctx.user.password = password || ctx.user.password;

  const [validateErr] = await validate(ctx.user);
  if (validateErr) {
    assertError(stringify(validateErr), ErrorCode.Invalid_Arguments);
  }

  await userService.edit(ctx.user);
  ctx.body = pick(ctx.user, ['id', 'username', 'nickname', 'avatar', 'power']);
}

export async function getSelf (ctx: ILoggedInContext) {
  ctx.body = pick(ctx.user, ['id', 'username', 'nickname', 'avatar', 'power']);
}
