import { IsOptional, IsString } from 'class-validator';

export class SelectVpnNodeRequestDto {
  @IsString()
  lineCode!: string;

  @IsString()
  nodeId!: string;

  @IsOptional()
  @IsString()
  regionCode?: string;
}
