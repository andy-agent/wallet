import { IsString } from 'class-validator';

export class ReferralBindRequestDto {
  @IsString()
  referralCode!: string;
}
