import { validate } from 'class-validator';
import { pick } from 'lodash';
import { relative } from 'path';
import { publicDir } from '../config';
import { assert, assertError } from '../lib/assert';
import ErrorCode from '../lib/ErrorCode';
import { ILoggedInContext } from '../lib/middlewares';
import { stringify } from '../lib/stringifyValidateError';
import { Post } from '../model/entity/Post';
import { postService } from '../service/index';

export async function newPost (ctx: ILoggedInContext) {
  // tslint:disable:prefer-const
  let { content, parameter, price }: { content: string; parameter: number; price: number } = ctx.request.body;
  price = Number(price);
  parameter = Number(parameter);
  const file = ctx.request.files[0];
  assert(file && file.path, 'image file required', ErrorCode.Missing_Files);
  assert(!isNaN(parameter), 'paremeter required', ErrorCode.Invalid_Arguments);

  const post = new Post();
  post.image = `/public/${relative(publicDir, file.path as string).replace(/\\/g, '/')}`;
  post.content = content;
  post.parameter = parameter;
  post.price = price;
  post.author = ctx.user;

  const [validateErr] = await validate(post);
  if (validateErr) {
    assertError(stringify(validateErr), ErrorCode.Invalid_Arguments);
  }

  await postService.newPost(post);
  ctx.body = formatPost(post);
  ctx.status = 200;
}

export async function removePost (ctx: ILoggedInContext) {
  const { pid }: { pid: number } = ctx.params;

  assert(!isNaN(pid), 'invalid post id', pid);

  await postService.removePost(ctx.user, pid);
  ctx.status = 200;
}

export async function getPost (ctx: ILoggedInContext) {
  let { pid }: { pid: number } = ctx.params;
  pid = Number(pid);
  assert(!isNaN(pid), 'invalid post id.', ErrorCode.Invalid_Arguments);

  const post = await postService.getOne(pid);
  if (!post) {
    assertError('post not exists.', ErrorCode.Not_Found);
  } else {
    ctx.body = formatPost(post);
  }
}

type GetPostType = 'all' | 'unlocked' | 'mine';

export async function getPosts (ctx: ILoggedInContext) {
  let { limit, before, filter }: { limit: number; before: Date; filter: GetPostType } = ctx.query;
  limit = Number(limit) || 30;
  before = new Date(before);
  filter = filter || 'all';
  if (isNaN(before.getFullYear())) { // invalid date
    before = new Date();
  }

  switch (filter) {
    case 'all':
      ctx.body = (await postService.getAllPost(limit, before)).map(formatPost);
      break;
    case 'unlocked':
      ctx.body = (await postService.getAllUnlocked(ctx.user.id, limit, before)).map(formatPost);
      break;
    case 'mine':
      ctx.body = (await postService.getOnesPost(ctx.user.id, limit, before)).map(formatPost);
      break;
    default:
      assertError(`invalid filter '${filter}'`, ErrorCode.Invalid_Arguments);
  }
}

export async function getOnesPosts (ctx: ILoggedInContext) {
  const { uid }: { uid: number } = ctx.params;
  assert(!isNaN(uid), 'invalid uid', ErrorCode.Invalid_Arguments);

  let { limit, before }: { limit: number; before: Date } = ctx.query;
  limit = Number(limit) || 30;
  before = new Date(before);
  if (isNaN(before.getFullYear())) { // invalid date
    before = new Date();
  }

  ctx.body = (await postService.getOnesPost(uid, limit, before)).map(formatPost);
}

function formatPost (post: Post): any {
  return {
    ...post,
    author: pick(post.author, ['id', 'nickname', 'username', 'avatar'])
  };
}
