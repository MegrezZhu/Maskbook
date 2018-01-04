import { IsDate, IsDefined, IsNumber, IsOptional, IsString, Min } from 'class-validator';
import { Column, CreateDateColumn, Entity, Index, JoinColumn, OneToOne, PrimaryGeneratedColumn } from 'typeorm';
import { User } from './User';

@Entity()
export class Post {
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

  @Index('post-date')
  @CreateDateColumn()
  @IsOptional()
  @IsDate()
  public date: Date;

  @OneToOne(() => User, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'u_id' })
  @IsDefined()
  public author: User;

  @Column({ nullable: false })
  @IsDefined()
  @IsNumber()
  @Min(0)
  public price: number;

  @Column({ default: '' })
  @IsOptional()
  @IsString()
  public content: string;
}
