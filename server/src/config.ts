import { join, resolve } from 'path';
import { MysqlConnectionOptions } from 'typeorm/driver/mysql/MysqlConnectionOptions';

export const serverRoot = join(__dirname, '..');

export const imageRootDir = resolve(__dirname, '../images');
export const postImageDir = resolve(imageRootDir, './post');
export const avatarImageDir = resolve(imageRootDir, './avatar');

export const isDev = process.env.NODE_ENV !== 'production';

export const mysqlConfig = {
  host: 'localhost',
  port: 3306,
  username: 'root',
  password: 'root',
  database: 'maskbook'
};

// the paths are supposed to be relative to cwd
const modelPath = resolve(__dirname, './model');

export const typeormConfig: MysqlConnectionOptions = {
  type: 'mysql',
  ...mysqlConfig,
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
