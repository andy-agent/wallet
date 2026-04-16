import { IsEmail, IsOptional, IsString, MinLength } from 'class-validator';

export class RegisterEmailRequestDto {
  @IsEmail()
  email!: string;

  @IsOptional()
  @IsString()
  code?: string;

  @IsString()
  @MinLength(8)
  password!: string;

  @IsOptional()
  @IsString()
  installationId?: string;
}
