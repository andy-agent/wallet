import { AddressService } from './address.service';
import { GenerateAddressRequestDto } from './dto/generate-address.request';
export declare class AddressController {
    private readonly addressService;
    constructor(addressService: AddressService);
    generateAddress(body: GenerateAddressRequestDto): {
        [x: string]: any;
    };
    getAddress(accountId: string): {
        [x: string]: any;
    };
}
