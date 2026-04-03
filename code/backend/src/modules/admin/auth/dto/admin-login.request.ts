import { IsString } from 'class-validator';

export class AdminLoginRequestDto {
  @IsString()
  username: string;

  @IsString()
  password: string;
}
