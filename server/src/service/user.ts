import { assert, assertError } from '../lib/assert';
import ErrorCode from '../lib/ErrorCode';
import { User } from '../model/entity/User';
import { repo } from '../model/index';

export async function regist (user: User): Promise<void> {
  assert(await repo.user.count({ nickname: user.nickname }) === 0, 'duplicate nickname', ErrorCode.Duplicate_Nickname);
  assert(await repo.user.count({ username: user.username }) === 0, 'duplicate username', ErrorCode.Duplicate_Username);

  await repo.user.save(user);
}

export async function login (username: string, password: string): Promise<User | null> {
  const user = await repo.user.findOne({ username, password }) || null;
  return user;
}

export async function edit (user: User): Promise<void> {
  const res = await repo.user
    .createQueryBuilder('user')
    .where('user.nickname = :nickname', user)
    .select(['user.id as id'])
    .getRawOne();
  if (res && res.id !== user.id) {
    assertError('duplicate nickname', ErrorCode.Duplicate_Nickname);
  }

  await repo.user.save(user);
}
