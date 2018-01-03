import { Column, Entity, JoinColumn, OneToOne, PrimaryGeneratedColumn } from 'typeorm';
import User from './User';

@Entity()
class Post {
  @PrimaryGeneratedColumn({ name: 'p_id' })
  public id: number;

  @Column()
  public image: string; // url

  @Column()
  public parameter: number; // parameter for image blurring

  @Column({ type: 'datetime', default: () => 'CURRENT_TIMESTAMP' })
  public date: Date;

  @OneToOne(() => User, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'u_id' })
  public by: User;
}

export default Post;
