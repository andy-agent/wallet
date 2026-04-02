import { IsEmail } from 'class-validator';

export class PasswordResetCodeRequestDto {
  @IsEmail()
  email!: string;
}
