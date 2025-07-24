package com.mariuszilinskas.vsp.users.account.mapper;

import com.mariuszilinskas.vsp.users.account.dto.UpdateAddressRequest;
import com.mariuszilinskas.vsp.users.account.enums.AddressType;
import com.mariuszilinskas.vsp.users.account.model.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AddressMapperTest {

    private final UUID userId = UUID.randomUUID();
    private UpdateAddressRequest request;

    @BeforeEach
    void setup() {
        request = new UpdateAddressRequest(
                AddressType.BILLING,
                "Street 1",
                "Street 2",
                "City",
                "County",
                "Country",
                "AB12 3CD"
        );
    }

    @Test
    void testMapFromUpdateAddressRequest_Success() {
        // Act
        Address address = AddressMapper.mapFromUpdateAddressRequest(userId, request);

        // Assert
        assertEquals(userId, address.getUserId());
        assertEquals(request.addressType(), address.getAddressType());
        assertEquals(request.street1(), address.getStreet1());
        assertEquals(request.street2(), address.getStreet2());
        assertEquals(request.city(), address.getCity());
        assertEquals(request.county(), address.getCounty());
        assertEquals(request.country(), address.getCountry());
        assertEquals(request.postcode(), address.getPostcode());
    }

    @Test
    void testMapFromUpdateAddressRequestWithExistingAddress_Success() {
        // Arrange
        Address existing = new Address();
        existing.setUserId(userId);

        // Act
        Address updated = AddressMapper.mapFromUpdateAddressRequest(existing, request);

        // Assert
        assertEquals(userId, updated.getUserId());
        assertEquals(request.addressType(), updated.getAddressType());
        assertEquals(request.street1(), updated.getStreet1());
        assertEquals(request.street2(), updated.getStreet2());
        assertEquals(request.city(), updated.getCity());
        assertEquals(request.county(), updated.getCounty());
        assertEquals(request.country(), updated.getCountry());
        assertEquals(request.postcode(), updated.getPostcode());
    }

}
