import { join, relative } from 'path';
import { MysqlConnectionOptions } from 'typeorm/driver/mysql/MysqlConnectionOptions';
import { resolve } from 'url';

export const serverRoot = join(__dirname, '..');

export const isDev = process.env.NODE_ENV !== 'production';

export const mongoConfig = {
  host: 'localhost',
  port: 3306,
  username: 'root',
  password: 'root',
  database: 'maskbook'
};

// the paths are supposed to be relative to cwd
const modelPath = relative(process.cwd(), resolve(__dirname, 'model'));

export const typeormConfig: MysqlConnectionOptions = {
  type: 'mysql',
  ...mongoConfig,
  synchronize: true,
  logging: false,
  entities: [
    resolve(modelPath, 'entity/**/*')
  ],
  migrations: [
    resolve(modelPath, 'migration/**/*')
  ],
  subscribers: [
    resolve(modelPath, 'subscriber/**/*')
  ],
  cli: {
    entitiesDir: resolve(modelPath, 'entity'),
    migrationsDir: resolve(modelPath, 'migration'),
    subscribersDir: resolve(modelPath, 'subscriber')
  }
};

export const session = {
  // cookie secret key
  cookieKey: ['maskbook-cookie-key'],
  // cookie name
  key: 'maskbook-v1',
  // 1 year
  maxAge: 31536000000
};
