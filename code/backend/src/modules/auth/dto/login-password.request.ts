import { IsEmail, IsOptional, IsString, MinLength } from 'class-validator';

export class LoginPasswordRequestDto {
  @IsEmail()
  email!: string;

  @IsString()
  @MinLength(8)
  password!: string;

  @IsOptional()
  @IsString()
  installationId?: string;
}
