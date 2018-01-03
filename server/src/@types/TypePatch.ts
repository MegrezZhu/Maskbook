import 'koa';

declare module 'koa' {
  // tslint:disable-next-line
  interface Context {
    session: any;
  }
}
