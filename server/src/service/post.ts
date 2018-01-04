import { chain } from 'lodash';
import { assert } from '../lib/assert';
import ErrorCode from '../lib/ErrorCode';
import { Post } from '../model/entity/Post';
import { User } from '../model/entity/User';
import { repo } from '../model/index';

export async function newPost (post: Post) {
  await repo.post.save(post);
}

export async function removePost (user: User, pid: number) {
  const post = await repo.post.findOneById(pid, { relations: ['author'] });
  if (post) {
    assert(post.author.id === user.id, 'this post is not yours', ErrorCode.No_Permission);
    await repo.post.removeById(pid);
  }
}

export async function getOnesPost (uid: number, limit: number, before: Date): Promise<Post[]> {
  before.setMinutes(before.getMinutes() + before.getTimezoneOffset()); // walkaround typeorm timezone error
  const res = await repo.post.createQueryBuilder('post')
    .where('post.u_id = :uid and post.date <= :before', { uid, before })
    .limit(limit)
    .getMany();
  return chain(res).sortBy('date').reverse().value();
}
