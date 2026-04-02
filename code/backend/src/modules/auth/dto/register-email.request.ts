import { IsEmail, IsOptional, IsString, MinLength } from 'class-validator';

export class RegisterEmailRequestDto {
  @IsEmail()
  email!: string;

  @IsString()
  code!: string;

  @IsString()
  @MinLength(8)
  password!: string;

  @IsOptional()
  @IsString()
  installationId?: string;
}
