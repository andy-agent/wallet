import { Controller, Get } from '@nestjs/common';

@Controller()
export class AppController {
  @Get()
  getRoot() {
    return {
      service: 'cryptovpn-backend',
      status: 'ok',
      docs: '/api/docs',
    };
  }
}
