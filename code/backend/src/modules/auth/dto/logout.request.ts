import { IsOptional, IsString } from 'class-validator';

export class LogoutRequestDto {
  @IsOptional()
  @IsString()
  reason?: string;
}
