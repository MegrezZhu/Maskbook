import { Column, Entity, Index, PrimaryGeneratedColumn } from 'typeorm';

@Entity()
class User {
  @PrimaryGeneratedColumn({ name: 'u_id' })
  public id: number;

  @Column()
  @Index('unique-username', { unique: true })
  public username: string;

  @Column()
  @Index('unique-nickname', { unique: true })
  public nickname: string;

  @Column()
  public password: string;

  @Column({ default: 'no-avatar' })
  public avatar: string; // url

  @Column({ default: 0 })
  public power: number;
}

export default User;
