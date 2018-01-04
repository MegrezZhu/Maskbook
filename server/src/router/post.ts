import * as Router from 'koa-router';
import { postController } from '../controller/index';
import { loginGuard, postImageUploader } from '../lib/middlewares';

const router = new Router();

router
  .post('/posts', postImageUploader, loginGuard(postController.newPost));

export default router;
