import * as Router from 'koa-router';
import { postController } from '../controller/index';
import { loginGuard, postImageUploader } from '../lib/middlewares';

const router = new Router();

router
  .post('/posts', postImageUploader, loginGuard(postController.newPost))
  .delete('/posts/:pid', loginGuard(postController.removePost))
  .get('/posts', loginGuard(postController.getPosts))
  .get('/posts/:pid', loginGuard(postController.getPost))
  .get('/users/:uid/posts', loginGuard(postController.getOnesPosts))
  .post('/posts/:pid/like', loginGuard(postController.like))
  .delete('/posts/:pid/lick', loginGuard(postController.unlike))
  .post('/posts/:pid/unlock', loginGuard(postController.unlock));

export default router;
