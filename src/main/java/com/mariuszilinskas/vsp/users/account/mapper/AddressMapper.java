package com.mariuszilinskas.vsp.users.account.mapper;

import com.mariuszilinskas.vsp.users.account.dto.UpdateAddressRequest;
import com.mariuszilinskas.vsp.users.account.model.Address;

import java.util.UUID;

public class AddressMapper {

    public static Address mapFromUpdateAddressRequest(UUID userId, UpdateAddressRequest request) {
        Address address = new Address();
        address.setUserId(userId);
        return mapFromUpdateAddressRequest(address, request);
    }

    public static Address mapFromUpdateAddressRequest(Address address, UpdateAddressRequest request) {
        address.setAddressType(request.addressType());
        address.setStreet1(request.street1());
        address.setStreet2(request.street2());
        address.setCity(request.city());
        address.setCounty(request.county());
        address.setCountry(request.country());
        address.setPostcode(request.postcode());
        return address;
    }

}
