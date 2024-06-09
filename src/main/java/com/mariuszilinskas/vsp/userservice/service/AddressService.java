package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.dto.*;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    AddressResponse createAddress(UpdateAddressRequest request);

    List<AddressResponse> getAllAddresses(UUID userId);

    AddressResponse getAddress(UUID userId, UUID addressId);

    AddressResponse updateAddress(UUID userId, UUID addressId, UpdateAddressRequest request);

    void deleteAddress(UUID userId, UUID addressId);

    void deleteUserAddresses(UUID userId);

}
