import * as Router from 'koa-router';
import { userController } from '../controller/index';
import { avatarImageUploader, loginGuard } from '../lib/middlewares';

const router = new Router();

router
  .post('/join', avatarImageUploader, userController.regist)
  .post('/session', userController.login)
  .post('/logout', userController.logout)
  .get('/user', loginGuard(userController.getSelf))
  .put('/user', loginGuard(userController.edit));

export default router;
