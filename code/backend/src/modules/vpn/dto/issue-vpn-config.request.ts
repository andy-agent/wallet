import { IsIn, IsString } from 'class-validator';

export class IssueVpnConfigRequestDto {
  @IsString()
  regionCode!: string;

  @IsString()
  @IsIn(['global', 'rule'])
  connectionMode!: 'global' | 'rule';
}
