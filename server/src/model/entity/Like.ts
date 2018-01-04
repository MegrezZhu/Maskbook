import { IsDate, IsDefined } from 'class-validator';
import { CreateDateColumn, Entity, Index, JoinColumn, OneToOne, PrimaryGeneratedColumn } from 'typeorm';
import { Post } from './Post';
import { User } from './User';

@Entity()
@Index('unique-like', ['user', 'post'], { unique: true })
export class Like {
  @PrimaryGeneratedColumn({ name: 'l_id' })
  public id: number;

  @OneToOne(() => User, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'u_id' })
  @IsDefined()
  public user: User;

  @OneToOne(() => Post, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'p_id' })
  @IsDefined()
  public post: Post;

  @CreateDateColumn()
  @IsDefined()
  @IsDate()
  public date: Date;
}
