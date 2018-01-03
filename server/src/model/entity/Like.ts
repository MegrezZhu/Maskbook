import { Column, Entity, Index, JoinColumn, OneToOne, PrimaryGeneratedColumn } from 'typeorm';
import Post from './Post';
import User from './User';

@Entity()
@Index('unique-like', ['user', 'post'], { unique: true })
class Like {
  @PrimaryGeneratedColumn({ name: 'l_id' })
  public id: number;

  @OneToOne(() => User, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'u_id' })
  public user: User;

  @OneToOne(() => Post, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'p_id' })
  public post: Post;

  @Column({ type: 'datetime', default: () => 'CURRENT_TIMESTAMP' })
  public date: Date;
}

export default Like;
