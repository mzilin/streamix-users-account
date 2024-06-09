package com.mariuszilinskas.vsp.userservice.dto;

import com.mariuszilinskas.vsp.userservice.enums.AddressType;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        UUID userId,
        AddressType addressType,
        String street1,
        String street2,
        String city,
        String county,
        String postcode,
        String country
) {}
