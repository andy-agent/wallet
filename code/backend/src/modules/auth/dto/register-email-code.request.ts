import { IsEmail } from 'class-validator';

export class RegisterEmailCodeRequestDto {
  @IsEmail()
  email!: string;
}
