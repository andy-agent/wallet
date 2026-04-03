import { Controller, Get } from '@nestjs/common';
import { ApiTags, ApiOperation } from '@nestjs/swagger';

@ApiTags('Health')
@Controller('healthz')
export class HealthController {
  @Get()
  @ApiOperation({ summary: '服务健康检查' })
  getHealth() {
    return {
      status: 'healthy',
      service: 'sol-agent',
    };
  }
}
