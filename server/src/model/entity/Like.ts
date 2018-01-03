import { IsDate, IsDefined } from 'class-validator';
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
  @IsDefined()
  public user: User;

  @OneToOne(() => Post, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'p_id' })
  @IsDefined()
  public post: Post;

  @Column({ nullable: false, type: 'datetime', default: () => 'CURRENT_TIMESTAMP' })
  @IsDefined()
  @IsDate()
  public date: Date;
}

export default Like;
