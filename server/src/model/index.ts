import { createConnection } from 'typeorm';
import { Connection } from 'typeorm/connection/Connection';
import { EntityManager } from 'typeorm/entity-manager/EntityManager';
import { Repository } from 'typeorm/repository/Repository';
import { typeormConfig } from '../config';
import { Like } from './entity/Like';
import { Post } from './entity/Post';
import { Purchase } from './entity/Purchase';
import { User } from './entity/User';

class Repostories {
  public user: Repository<User>;
  public post: Repository<Post>;
  public like: Repository<Like>;
  public purchase: Repository<Purchase>;

  private conn: Connection;

  public async init () {
    this.conn = await createConnection(typeormConfig);

    this.user = this.conn.getRepository(User);
    this.post = this.conn.getRepository(Post);
    this.like = this.conn.getRepository(Like);
    this.purchase = this.conn.getRepository(Purchase);
  }

  public async transaction (task: (manager: EntityManager) => Promise<any>) {
    return this.conn.transaction(task);
  }
}

export const repo = new Repostories();
