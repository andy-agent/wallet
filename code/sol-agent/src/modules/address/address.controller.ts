import {
  Body,
  Controller,
  Get,
  Param,
  Post,
  UseGuards,
} from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { InternalAuthGuard } from '../../common/guards/internal-auth.guard';
import { AddressService } from './address.service';
import { GenerateAddressRequestDto } from './dto/generate-address.request';

@ApiTags('Address')
@UseGuards(InternalAuthGuard)
@ApiBearerAuth('x-internal-auth')
@Controller('internal/v1/address')
export class AddressController {
  constructor(private readonly addressService: AddressService) {}

  @Post()
  @ApiOperation({ summary: '生成 Solana 地址（占位）' })
  generateAddress(@Body() body: GenerateAddressRequestDto) {
    return this.addressService.generateAddress(body);
  }

  @Get(':accountId')
  @ApiOperation({ summary: '查询指定账户的 Solana 地址（占位）' })
  getAddress(@Param('accountId') accountId: string) {
    return this.addressService.getAddress(accountId);
  }
}
