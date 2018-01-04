import { assert } from '../lib/assert';
import ErrorCode from '../lib/ErrorCode';
import { Post } from '../model/entity/Post';
import { Purchase } from '../model/entity/Purchase';
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

export async function getOne (pid: number): Promise<Post | null> {
  const post = await repo.post.findOneById(pid, { relations: ['author'] }) || null;
  return post;
}

export async function getAllUnlocked (uid: number, limit: number, before: Date): Promise<Post[]> {
  const res = await repo.post.createQueryBuilder('post')
    .innerJoinAndSelect(Purchase, 'purchase', 'purchase.u_id = :uid and purchase.p_id = post.p_id', { uid })
    .innerJoinAndSelect('post.author', 'user')
    .where('post.date <= :before', { before: fixDate(before) })
    .orderBy('post.date', 'DESC')
    .limit(limit)
    .getMany();
  return res;
}

export async function getAllPost (limit: number, before: Date): Promise<Post[]> {
  const res = await repo.post.createQueryBuilder('post')
    .innerJoinAndSelect('post.author', 'user')
    .where('post.date <= :before', { before: fixDate(before) })
    .orderBy('post.date', 'DESC')
    .limit(limit)
    .getMany();
  return res;
}

export async function getOnesPost (uid: number, limit: number, before: Date): Promise<Post[]> {
  const res = await repo.post.createQueryBuilder('post')
    .innerJoinAndSelect('post.author', 'user')
    .where('post.u_id = :uid and post.date <= :before', { uid, before: fixDate(before) })
    .orderBy('post.date', 'DESC')
    .limit(limit)
    .getMany();
  return res;
}

// walkaround typeorm timezone error
function fixDate (date: Date): Date {
  const fixed = new Date(date);
  fixed.setMinutes(fixed.getMinutes() + fixed.getTimezoneOffset());
  return fixed;
}
