import { Controller, Get } from '@nestjs/common';

@Controller()
export class AppController {
  @Get()
  getRoot() {
    return {
      service: 'sol-agent',
      version: '0.0.1',
    };
  }
}
