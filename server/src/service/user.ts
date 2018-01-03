import { assert } from '../lib/assert';
import ErrorCode from '../lib/ErrorCode';
import { User } from '../model/entity/User';
import { repo } from '../model/index';

export async function regist (user: User) {
  assert(await repo.user.count({ nickname: user.nickname }) === 0, 'duplicate nickname', ErrorCode.Duplicate_Nickname);
  assert(await repo.user.count({ username: user.username }) === 0, 'duplicate username', ErrorCode.Duplicate_Username);

  await repo.user.save(user);
}
