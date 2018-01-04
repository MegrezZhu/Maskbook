import * as Router from 'koa-router';

import postRouter from './post';
import userRouter from './user';

const router = new Router();

router
  .use('/api', userRouter.routes())
  .use('/api', postRouter.routes());

export default router;
