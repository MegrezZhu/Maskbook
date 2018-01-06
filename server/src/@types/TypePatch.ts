import { ReadStream } from 'fs-extra';
import 'koa';
import 'lodash';
import { User } from '../model/entity/User';

declare module 'koa' {
  // tslint:disable-next-line
  interface Context {
    session: any;
    user: User | null;
  }

  // tslint:disable-next-line
  interface Request {
    files: ReadStream[];
  }
}

declare module 'lodash' {
  // tslint:disable-next-line
  interface LoDashStatic {
    zipWith<T, R, TResult>(
      arrays: List<T> | null | undefined,
      arrays2: List<R> | null | undefined,
      iteratee: (value1: T, value2: R) => TResult
    ): TResult[];
  }
}
