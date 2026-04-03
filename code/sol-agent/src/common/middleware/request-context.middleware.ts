import { Injectable, NestMiddleware } from '@nestjs/common';
import { Request, Response, NextFunction } from 'express';
import { randomUUID } from 'crypto';

@Injectable()
export class RequestContextMiddleware implements NestMiddleware {
  use(req: Request, res: Response, next: NextFunction) {
    (req as Request & { requestId?: string }).requestId =
      req.headers['x-request-id']?.toString() ?? randomUUID();
    next();
  }
}
