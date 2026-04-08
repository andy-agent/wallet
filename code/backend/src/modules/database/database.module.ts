import { Global, Module } from '@nestjs/common';
import { RuntimeStateRepository } from './runtime-state.repository';

@Global()
@Module({
  providers: [RuntimeStateRepository],
  exports: [RuntimeStateRepository],
})
export class DatabaseModule {}
