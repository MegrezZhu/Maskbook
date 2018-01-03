import * as Router from 'koa-router';
import { userController } from '../controller/index';

const router = new Router();

router
  .post('/join', userController.regist);

export default router;
