import { validate } from 'class-validator';
import { IRouterContext } from 'koa-router';
import { assertError } from '../lib/assert';
import ErrorCode from '../lib/ErrorCode';
import { IUser, User } from '../model/entity/User';
import { userService } from '../service/index';

export async function regist (ctx: IRouterContext) {
  const user = User.fromInterface((ctx.request.body || {}) as IUser);

  const [validateErr] = await validate(user);
  if (validateErr) {
    console.log(validateErr);
    assertError('invalid argument', ErrorCode.Invalid_Arguments);
  }

  await userService.regist(user);

  ctx.status = 200;
}
