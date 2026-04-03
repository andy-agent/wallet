import {
  ExceptionFilter,
  Catch,
  ArgumentsHost,
  HttpException,
  HttpStatus,
} from '@nestjs/common';
import { Request, Response } from 'express';

@Catch()
export class AllExceptionsFilter implements ExceptionFilter {
  catch(exception: unknown, host: ArgumentsHost): void {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();
    const request = ctx.getRequest<Request & { requestId?: string }>();

    const requestId = request.requestId ?? 'unknown';
    const status =
      exception instanceof HttpException
        ? exception.getStatus()
        : HttpStatus.INTERNAL_SERVER_ERROR;

    const payload =
      exception instanceof HttpException ? exception.getResponse() : null;

    const code =
      typeof payload === 'object' && payload && 'code' in payload
        ? String(payload.code)
        : status >= 500
          ? 'INTERNAL_ERROR'
          : 'HTTP_ERROR';

    const message =
      typeof payload === 'object' && payload && 'message' in payload
        ? String(payload.message)
        : exception instanceof Error
          ? exception.message
          : 'Unexpected error';

    const details =
      typeof payload === 'object' && payload && 'details' in payload
        ? payload.details
        : undefined;

    response.status(status).json({
      requestId,
      code,
      message,
      ...(details !== undefined ? { details } : {}),
    });
  }
}
