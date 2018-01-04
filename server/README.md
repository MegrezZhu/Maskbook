# Maskbook Server

## Development Guide

* In VSCode, use `Tasks: Run Task` and:
  * select `tsc: watch - tsconfig.json` to start TypeScript live-compiling.
  * select `tsc: build - tsconfig.json` for one-time compiling.
* Compiled `.js` files are in `./dist`.
* If some modules lack definition, simply add the following stub code in `./src/@types` with file name `xxx.d.ts`, in which `xxx` is arbitary:
```typescript
declare module 'MODULE-NAME';
```
* Recommended VSCode Extensions:
  * TSLint
    * enable autofix: `"tslint.autoFixOnSave": true`
    * to avoid conflicts with editor's autofix `"editor.formatOnSave": true`, add both `"typescript.format.insertSpaceBeforeFunctionParenthesis": true` & `"typescript.format.insertSpaceAfterConstructor": true`

## API
### 实体
400时返回错误信息JSON
```
{
  errno: number
  message: string
}
```

### 用户相关
#### 注册
```
POST /api/join
body {
  username: string (required)
  password: string (required)
  nickname: string
  avatar: string
}

200
{
  id: number
  username: string
  nickname: string
  avatar: string
  power: number
}
```

#### 登录
```
POST /api/session
body {
  username: string (required)
  password: string (required)
}

200
{
  id: number
  username: string
  nickname: string
  avatar: string
  power: number
}
```

#### 修改信息
```
PUT /api/user
提交需要修改的字段即可
body {
  nickname: string
  avatar: string
}

200
{
  id: number
  username: string
  nickname: string
  avatar: string
  power: number
}
```

#### 获取信息
```
GET /api/user

200
{
  id: number
  username: string
  nickname: string
  avatar: string
  power: number
}
```

### 消息相关
#### 发布
```
POST /api/posts
body {
  content: string
  image: string (required)
  parameter: number (required)
  price: number (required)
}

200
{
  id: number
  author: {
    id: number
    username: string
    nickname: string
    avatar: string
  }
  content: string
  image: string
  parameter: number
  price: number
  date: date
}
```

#### 删除
```
DELETE /api/posts/:id
path {
  id: number (required)
}

200
```

#### 获取列表
```
GET /api/posts
query {
  limit: number (default 30)
  filter: all, unlocked, mine (default all)
  before: date （default now)
}

200
[
  {
    id: number
    author: {
      id: number
      username: string
      nickname: string
      avatar: string
    }
    content: string
    image: string
    parameter: number
    price: number
    date: date
    unlock: boolean
    like: boolean
  }
]
```

#### 获取某人的发布
```
GET /api/users/:id/posts
query {
  limit: number (default 30)
  before: date (default now)
}

200
[
  {
    id: number
    author: {
      id: number
      username: string
      nickname: string
      avatar: string
    }
    content: string
    image: string
    parameter: number
    price: number
    date: date
    unlock: boolean
    like: boolean
  }
]
```

#### 获取详情
```
GET /api/posts/:id
path {
  id: number (required)
}

200
{
  id: number
  author: {
    id: number
    username: string
    nickname: string
    avatar: string
  }
  content: string
  image: string
  parameter: number
  price: number
  date: date
  unlock: boolean
  like: boolean
}
```

### 点赞相关
#### 点赞
```
POST /api/posts/:id/like
path {
  id: number (required)
}

200
```

#### 取消点赞
```
DELETE /api/posts/:id/lick
path {
  id: number (required)
}

200
```

### 抛瓦相关
#### 解锁
```
POST /api/posts/:id/unlock
path {
  id: number (required)
}

200
```
