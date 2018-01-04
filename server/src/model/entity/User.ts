import { IsDefined, IsNumber, IsString, Length, Min } from 'class-validator';
import { Column, Entity, Index, PrimaryGeneratedColumn } from 'typeorm';

@Entity()
export class User {
  public static fromInterface (iUser: IUser): User {
    const user = new User();

    user.id = iUser.id;
    user.username = iUser.username;
    user.password = iUser.password;
    user.nickname = iUser.nickname;
    user.avatar = iUser.avatar || user.avatar;
    user.power = iUser.power || user.power;

    return user;
  }

  @PrimaryGeneratedColumn({ name: 'u_id' })
  public id: number;

  @Column({ nullable: false })
  @Index('unique-username', { unique: true })
  @IsDefined()
  @IsString()
  @Length(6, 20)
  public username: string;

  @Column({ nullable: false })
  @Index('unique-nickname', { unique: true })
  @IsDefined()
  @IsString()
  @Length(2, 20)
  public nickname: string;

  @Column({ nullable: false })
  @IsDefined()
  @IsString()
  @Length(6, 20)
  public password: string;

  @Column({ default: 'no-avatar', nullable: false })
  @IsDefined()
  @IsString()
  public avatar: string = '/public/images/default-avatar.png';

  @Column({ default: 0, nullable: false })
  @IsDefined()
  @IsNumber()
  @Min(0)
  public power: number = 0;
}

// tslint:disable-next-line
export interface IUser extends User { }
