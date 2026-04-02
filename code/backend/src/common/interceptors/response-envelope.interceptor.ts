import {
  CallHandler,
  ExecutionContext,
  Injectable,
  NestInterceptor,
} from '@nestjs/common';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class ResponseEnvelopeInterceptor implements NestInterceptor {
  intercept(context: ExecutionContext, next: CallHandler): Observable<unknown> {
    const request = context
      .switchToHttp()
      .getRequest<{ requestId?: string }>();

    return next.handle().pipe(
      map((data) => {
        if (
          data &&
          typeof data === 'object' &&
          'requestId' in (data as Record<string, unknown>) &&
          'code' in (data as Record<string, unknown>) &&
          'message' in (data as Record<string, unknown>)
        ) {
          return data;
        }

        return {
          requestId: request.requestId ?? 'unknown',
          code: 'OK',
          message: 'ok',
          data,
        };
      }),
    );
  }
}
