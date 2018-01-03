import * as Router from 'koa-router';
import { userController } from '../controller/index';
import { loginGuard } from '../lib/middlewares';

const router = new Router();

router
  .post('/join', userController.regist)
  .post('/session', userController.login)
  .put('/user', loginGuard(userController.edit));

export default router;
