import { createConnection } from 'typeorm';
import { Repository } from 'typeorm/repository/Repository';
import User from './entity/User';

class Repostories {
  public user: Repository<User>;

  public async init () {
    const conn = await createConnection();

    this.user = conn.getRepository(User);
  }
}

export const repo = new Repostories();
