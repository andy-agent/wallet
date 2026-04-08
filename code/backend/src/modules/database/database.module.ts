import { Global, Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { FileRuntimeStateRepository } from './file-runtime-state.repository';
import { PostgresRuntimeStateRepository } from './postgres-runtime-state.repository';
import { resolveRuntimeStateConfig } from './runtime-state.config';
import { RuntimeStateRepository } from './runtime-state.repository';

@Global()
@Module({
  imports: [ConfigModule],
  providers: [
    {
      provide: RuntimeStateRepository,
      inject: [ConfigService],
      useFactory: async (configService: ConfigService) => {
        const config = resolveRuntimeStateConfig(configService);

        if (config.backend === 'file') {
          return new FileRuntimeStateRepository(config.filePath);
        }

        const repository = new PostgresRuntimeStateRepository({
          connectionString: config.databaseUrl!,
        });
        await repository.initialize();
        return repository;
      },
    },
  ],
  exports: [RuntimeStateRepository],
})
export class DatabaseModule {}
