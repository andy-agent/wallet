import { IsOptional, IsString } from 'class-validator';

export class RefreshOrderStatusRequestDto {
  @IsOptional()
  @IsString()
  clientObservedStatus?: string;
}
