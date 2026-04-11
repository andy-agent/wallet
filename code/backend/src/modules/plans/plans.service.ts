import { Injectable } from '@nestjs/common';
import { ClientCatalogService } from '../database/client-catalog.service';

@Injectable()
export class PlansService {
  constructor(private readonly clientCatalogService: ClientCatalogService) {}

  async listPlans() {
    return {
      items: await this.clientCatalogService.listPlans({
        status: 'ACTIVE',
      }),
    };
  }
}
