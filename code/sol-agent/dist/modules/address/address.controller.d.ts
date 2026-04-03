import { AddressService } from './address.service';
import { GenerateAddressRequestDto } from './dto/generate-address.request';
export declare class AddressController {
    private readonly addressService;
    constructor(addressService: AddressService);
    generateAddress(body: GenerateAddressRequestDto): {
        accountId: string;
        networkCode: string;
        address: string;
        publicKey: string;
        createdAt: string;
    };
    getAddress(accountId: string): any;
}
