import { NestMiddleware } from '@nestjs/common';
import { NextFunction, Request, Response } from 'express';
type RequestWithContext = Request & {
    requestId?: string;
};
export declare class RequestContextMiddleware implements NestMiddleware {
    use(req: RequestWithContext, res: Response, next: NextFunction): void;
}
export {};
