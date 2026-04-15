import { Logger, ValidationPipe } from '@nestjs/common';
import { NestFactory } from '@nestjs/core';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { AppModule } from './app.module';
import { AllExceptionsFilter } from './common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from './common/interceptors/response-envelope.interceptor';

async function bootstrap() {
  const bootstrapLogger = new Logger('Bootstrap');
  const app = await NestFactory.create(AppModule);
  app.setGlobalPrefix('api');
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      transform: true,
      forbidUnknownValues: false,
    }),
  );
  app.useGlobalInterceptors(new ResponseEnvelopeInterceptor());
  app.useGlobalFilters(new AllExceptionsFilter());

  const swaggerConfig = new DocumentBuilder()
    .setTitle('CryptoVPN Backend')
    .setDescription('Foundation API for CryptoVPN')
    .setVersion('0.1.0')
    .build();
  const document = SwaggerModule.createDocument(app, swaggerConfig);
  SwaggerModule.setup('api/docs', app, document);

  const port = Number(process.env.PORT ?? 3000);
  const runtimeFingerprint = describeRuntimeFingerprint();
  bootstrapLogger.log(
    `Runtime state config: backend=${runtimeFingerprint.backend}; database=${runtimeFingerprint.database}; redis=${runtimeFingerprint.redis}`,
  );
  await app.listen(port);
}

function describeRuntimeFingerprint() {
  return {
    backend: process.env.RUNTIME_STATE_BACKEND ?? 'auto',
    database: maskUrl(process.env.DATABASE_URL),
    redis: maskUrl(process.env.REDIS_URL),
  };
}

function maskUrl(rawUrl?: string) {
  if (!rawUrl) {
    return 'unset';
  }

  try {
    const parsed = new URL(rawUrl);
    const port = parsed.port || defaultPortForProtocol(parsed.protocol);
    const database = parsed.pathname.replace(/^\/+/, '') || '(none)';
    return `${parsed.protocol}//${parsed.hostname}:${port}/${database}`;
  } catch {
    return 'invalid';
  }
}

function defaultPortForProtocol(protocol: string) {
  switch (protocol) {
    case 'postgres:':
    case 'postgresql:':
      return '5432';
    case 'redis:':
    case 'rediss:':
      return protocol === 'rediss:' ? '6380' : '6379';
    default:
      return '';
  }
}

bootstrap();
