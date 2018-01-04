import { Post } from '../model/entity/Post';
import { repo } from '../model/index';

export async function newPost (post: Post) {
  await repo.post.save(post);
}
