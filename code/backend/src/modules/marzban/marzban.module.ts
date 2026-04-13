import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { MarzbanService } from './marzban.service';

@Module({
  imports: [ConfigModule],
  providers: [MarzbanService],
  exports: [MarzbanService],
})
export class MarzbanModule {}
