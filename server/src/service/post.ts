import { zipWith } from 'lodash';
import { assert, assertError } from '../lib/assert';
import ErrorCode from '../lib/ErrorCode';
import { Like } from '../model/entity/Like';
import { Post } from '../model/entity/Post';
import { Purchase } from '../model/entity/Purchase';
import { User } from '../model/entity/User';
import { repo } from '../model/index';

export async function newPost (post: Post) {
  await repo.post.save(post);
}

export async function removePost (self: User, pid: number) {
  const post = await repo.post.findOneById(pid, { relations: ['author'] });
  if (post) {
    assert(post.author.id === self.id, 'this post is not yours', ErrorCode.No_Permission);
    await repo.post.removeById(pid);
  }
}

export async function getOne (selfId: number, pid: number): Promise<UPost | null> {
  const post = await repo.post.findOneById(pid, { relations: ['author'] }) as UPost || null;
  if (post) {
    post.unlocked = 1 === await repo.purchase.createQueryBuilder('pur')
      .where('pur.u_id = :selfId and pur.p_id = :pid', { selfId, pid })
      .getCount();
    post.like = 1 === await repo.like.createQueryBuilder('like')
      .where('like.u_id = :selfId and like.p_id = :pid', { selfId, pid })
      .getCount();
  }
  return post;
}

export async function getAllUnlocked (selfId: number, limit: number, before: Date): Promise<UPost[]> {
  const { raw, entities } = await repo.post.createQueryBuilder('post')
    .innerJoinAndSelect(Purchase, 'purchase', 'purchase.u_id = :selfId and purchase.p_id = post.p_id', { selfId })
    .innerJoinAndSelect('post.author', 'user')
    .leftJoinAndSelect(Like, 'like', 'like.u_id = :selfId and like.p_id = post.p_id', { selfId })
    .where('post.date < :before', { before: fixDate(before) })
    .orderBy('post.date', 'DESC')
    .limit(limit)
    .getRawAndEntities();
  return zip(entities, raw);
}

export async function getAllPost (selfId: number, limit: number, before: Date): Promise<UPost[]> {
  const { raw, entities } = await repo.post.createQueryBuilder('post')
    .innerJoinAndSelect('post.author', 'user')
    .leftJoinAndSelect(Purchase, 'purchase', 'purchase.u_id = :selfId and purchase.p_id = post.p_id', { selfId })
    .leftJoinAndSelect(Like, 'like', 'like.u_id = :selfId and like.p_id = post.p_id', { selfId })
    .where('post.date <= :before', { before: fixDate(before) })
    .orderBy('post.date', 'DESC')
    .limit(limit)
    .getRawAndEntities();
  return zip(entities, raw);
}

export async function getOnesPost (selfId: number, uid: number, limit: number, before: Date): Promise<Post[]> {
  const { raw, entities } = await repo.post.createQueryBuilder('post')
    .innerJoinAndSelect('post.author', 'user')
    .leftJoinAndSelect(Purchase, 'purchase', 'purchase.u_id = :selfId and purchase.p_id = post.p_id', { selfId })
    .leftJoinAndSelect(Like, 'like', 'like.u_id = :selfId and like.p_id = post.p_id', { selfId })
    .where('post.u_id = :uid and post.date < :before', { uid, before: fixDate(before) })
    .orderBy('post.date', 'DESC')
    .limit(limit)
    .getRawAndEntities();
  return zip(entities, raw);
}

export async function newLike (user: User, pid: number) {
  try {
    const post = await repo.post.findOneById(pid);
    if (!post) {
      assertError('post not exists.', ErrorCode.Not_Found);
    } else {
      const like = new Like();
      like.post = post;
      like.user = user;
      await repo.like.save(like);
    }
  } catch (err) {
    // ignore duplicate-like exception
    if (err.errno !== 1062 || err.sqlState !== '23000') {
      throw err;
    }
  }
}

export async function removeLike (user: User, pid: number) {
  await repo.like.createQueryBuilder('like')
    .delete()
    .where('like.u_id = :uid and like.p_id = :pid', { uid: user.id, pid })
    .execute();
}

export async function unlock (user: User, pid: number) {
  await repo.transaction(async entityManager => {
    // stupid, serial way
    const post = await entityManager.findOneById(Post, pid, { relations: ['author'] });
    assert(post, 'post not exists.', ErrorCode.Not_Found);

    if (!await entityManager.findOne(Purchase, { user: user.id as any, post: post!.id as any })) { // walkaround
      // not purchased yet, create one
      const purchase = new Purchase();
      purchase.post = post!;
      purchase.user = user;
      await entityManager.save(purchase);

      const price = post!.price;
      user.power = Math.max(0, user.power - price);
      const oUser = post!.author;
      oUser.power = oUser.power + price * 0.5;

      await Promise.all([
        entityManager.save(user),
        entityManager.save(oUser)
      ]);
    }
  });
}

// walkaround typeorm timezone error
function fixDate (date: Date): Date {
  const fixed = new Date(date);
  fixed.setMinutes(fixed.getMinutes() + fixed.getTimezoneOffset());
  return fixed;
}

type UPost = Post & { unlocked: boolean; like: boolean };

function zip (posts: Post[], raws: any[]): UPost[] {
  return zipWith(posts, raws, (post, raw) => {
    const uPost = post as UPost;
    uPost.unlocked = raw.purchase_pr_id ? true : false;
    uPost.like = raw.like_l_id ? true : false;
    return uPost;
  });
}
