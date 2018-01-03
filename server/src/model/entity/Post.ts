import { IsDate, IsDefined, IsNumber, IsOptional, IsString, Min } from 'class-validator';
import { Column, Entity, JoinColumn, OneToOne, PrimaryGeneratedColumn } from 'typeorm';
import User from './User';

@Entity()
class Post {
  @PrimaryGeneratedColumn({ name: 'p_id' })
  public id: number;

  @Column({ nullable: false })
  @IsDefined()
  @IsString()
  public image: string; // url

  @Column({ nullable: false, default: 0 })
  @IsDefined()
  @IsNumber()
  public parameter: number; // parameter for image blurring

  @Column({ nullable: false, type: 'datetime', default: () => 'CURRENT_TIMESTAMP' })
  @IsOptional()
  @IsDate()
  public date: Date;

  @OneToOne(() => User, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'u_id' })
  @IsDefined()
  public by: User;

  @Column({ nullable: false })
  @IsDefined()
  @IsNumber()
  @Min(0)
  public price: number;
}

export default Post;
