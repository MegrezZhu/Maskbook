import { IsDefined, IsNumber, Min, IsOptional, IsDate } from 'class-validator';
import { Column, Entity, Index, JoinColumn, OneToOne, PrimaryGeneratedColumn } from 'typeorm';
import Post from './Post';
import User from './User';

@Entity()
@Index('unique-purchase', ['user', 'post'], { unique: true })
class Purchase {
  @PrimaryGeneratedColumn({ name: 'pr_id' })
  public id: number;

  @OneToOne(() => User, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'u_id' })
  @IsDefined()
  public user: User;

  @OneToOne(() => Post, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'p_id' })
  @IsDefined()
  public post: Post;

  @Column({ nullable: false })
  @IsDefined()
  @IsNumber()
  @Min(0)
  public power: number; // power used in this purchase

  @Column({ nullable: false, type: 'datetime', default: () => 'CURRENT_TIMESTAMP' })
  @IsOptional()
  @IsDate()
  public date: Date;
}

export default Purchase;
