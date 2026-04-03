import { Injectable, NestMiddleware } from '@nestjs/common';
import { randomUUID } from 'crypto';
import { NextFunction, Request, Response } from 'express';

@Injectable()
export class RequestContextMiddleware implements NestMiddleware {
  use(req: Request, res: Response, next: NextFunction) {
    (req as unknown as Record<string, string>).requestId =
      req.headers['x-request-id']?.toString() ?? randomUUID();
    next();
  }
}
