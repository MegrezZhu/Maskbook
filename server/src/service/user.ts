import { User } from '../model/entity/User';
import { repo } from '../model/index';

export async function regist (user: User) {
  try {
    await repo.user.save(user);
  } catch (err) {
    throw err;
  }
}
