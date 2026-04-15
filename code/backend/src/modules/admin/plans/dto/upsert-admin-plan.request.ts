import {
  ArrayUnique,
  IsArray,
  IsBoolean,
  IsIn,
  IsInt,
  IsOptional,
  IsString,
  Matches,
  MaxLength,
  Min,
} from 'class-validator';
import { Type } from 'class-transformer';

export class UpsertAdminPlanRequestDto {
  @IsString()
  @MaxLength(32)
  planCode!: string;

  @IsString()
  @MaxLength(64)
  name!: string;

  @IsOptional()
  @IsString()
  description?: string | null;

  @Type(() => Number)
  @IsInt()
  @Min(1)
  billingCycleMonths!: number;

  @IsString()
  @Matches(/^\d+(\.\d{1,8})?$/)
  priceUsd!: string;

  @IsBoolean()
  isUnlimitedTraffic!: boolean;

  @Type(() => Number)
  @IsInt()
  @Min(1)
  maxActiveSessions!: number;

  @IsString()
  @IsIn(['BASIC_ONLY', 'INCLUDE_ADVANCED', 'CUSTOM'])
  regionAccessPolicy!: 'BASIC_ONLY' | 'INCLUDE_ADVANCED' | 'CUSTOM';

  @IsBoolean()
  includesAdvancedRegions!: boolean;

  @IsArray()
  @ArrayUnique()
  @IsString({ each: true })
  allowedRegionIds!: string[];

  @Type(() => Number)
  @IsInt()
  @Min(0)
  displayOrder!: number;

  @IsString()
  @IsIn(['DRAFT', 'ACTIVE', 'DISABLED'])
  status!: 'DRAFT' | 'ACTIVE' | 'DISABLED';
}
