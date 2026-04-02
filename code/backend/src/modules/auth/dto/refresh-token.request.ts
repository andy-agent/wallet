import { IsOptional, IsString } from 'class-validator';

export class RefreshTokenRequestDto {
  @IsString()
  refreshToken!: string;

  @IsOptional()
  @IsString()
  installationId?: string;
}
