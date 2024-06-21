package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.dto.*;
import com.mariuszilinskas.vsp.userservice.model.Address;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    Address createAddress(UUID userId, UpdateAddressRequest request);

    List<Address> getAllAddresses(UUID userId);

    Address getAddress(UUID userId, UUID addressId);

    Address updateAddress(UUID userId, UUID addressId, UpdateAddressRequest request);

    void deleteAddress(UUID userId, UUID addressId);

    void deleteUserAddresses(UUID userId);

}
