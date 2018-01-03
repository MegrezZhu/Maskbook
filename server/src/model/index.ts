import { createConnection } from 'typeorm';
import { Repository } from 'typeorm/repository/Repository';
import { typeormConfig } from '../config';
import Like from './entity/Like';
import Post from './entity/Post';
import Purchase from './entity/Purchase';
import User from './entity/User';

class Repostories {
  public user: Repository<User>;
  public post: Repository<Post>;
  public like: Repository<Like>;
  public purchase: Repository<Purchase>;

  public async init () {
    const conn = await createConnection(typeormConfig);

    this.user = conn.getRepository(User);
    this.post = conn.getRepository(Post);
    this.like = conn.getRepository(Like);
    this.purchase = conn.getRepository(Purchase);
  }
}

export const repo = new Repostories();
