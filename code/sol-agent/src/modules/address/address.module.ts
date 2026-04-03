import { Module } from '@nestjs/common';
import { SolanaModule } from '../solana/solana.module';
import { AddressController } from './address.controller';
import { AddressService } from './address.service';

@Module({
  imports: [SolanaModule],
  controllers: [AddressController],
  providers: [AddressService],
  exports: [AddressService],
})
export class AddressModule {}
