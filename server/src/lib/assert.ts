import ErrorCode from './ErrorCode';

export interface IAssertionError extends Error {
  errno: number;
  expose: boolean;
}

export function assert (expr: any, message: string, code = ErrorCode.Unknown) {
  if (!expr) {
    const err = new Error(message) as IAssertionError;
    err.errno = code;
    err.expose = true;
    throw err;
  }
}

export function assertError (message: string, code = ErrorCode.Unknown) {
  assert(false, message, code);
}

export function exposeThrow (err: Error, code = ErrorCode.Unknown) {
  const wrapped = err as IAssertionError;
  wrapped.errno = code;
  wrapped.expose = true;
  throw wrapped;
}
