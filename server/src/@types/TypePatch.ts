import 'koa';
import { User } from '../model/entity/User';

declare module 'koa' {
  // tslint:disable-next-line
  interface Context {
    session: any;
    user: User | null;
  }
}
