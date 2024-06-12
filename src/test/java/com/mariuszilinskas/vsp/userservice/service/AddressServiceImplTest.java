package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.dto.*;
import com.mariuszilinskas.vsp.userservice.enums.AddressType;
import com.mariuszilinskas.vsp.userservice.exception.*;
import com.mariuszilinskas.vsp.userservice.model.Address;
import com.mariuszilinskas.vsp.userservice.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private UpdateAddressRequest createRequest;
    private UpdateAddressRequest updateRequest;
    private final UUID userId = UUID.randomUUID();
    private final UUID addressId = UUID.randomUUID();
    private final Address address = new Address();
    private final Address address2 = new Address();

    // ------------------------------------

    @BeforeEach
    void setUp() {
        address.setId(addressId);
        address.setUserId(userId);
        address.setAddressType(AddressType.BILLING);
        address.setStreet1("1 Street");
        address.setStreet2(null);
        address.setCity("London");
        address.setCounty("Camden");
        address.setCountry("United Kingdom");
        address.setPostcode("NW1 5HR");

        address2.setId(UUID.randomUUID());
        address2.setUserId(userId);
        address2.setAddressType(AddressType.SHIPPING);
        address2.setStreet1("5 Street");
        address2.setStreet2(null);
        address2.setCity("London");
        address2.setCounty("Edmonton");
        address2.setCountry("United Kingdom");
        address2.setPostcode("N9 6GH");

        createRequest = new UpdateAddressRequest(
                address.getAddressType(),
                address.getStreet1(),
                address.getStreet2(),
                address.getCity(),
                address.getCounty(),
                address.getPostcode(),
                address.getCountry()
        );

        updateRequest = new UpdateAddressRequest(
                address.getAddressType(),
                "5 Street",
                address.getStreet2(),
                "Manchester",
                "",
                "M1 5GH",
                address.getCountry()
        );
    }

    // ------------------------------------

    @Test
    void testCreateAddress_Success() {
        // Arrange
        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);

        when(addressRepository.existsByUserIdAndAddressType(userId, createRequest.addressType())).thenReturn(false);
        when(addressRepository.save(captor.capture())).thenReturn(address);

        // Act
        Address response = addressService.createAddress(userId, createRequest);

        // Assert
        assertNotNull(response);
        assertEquals(address.getId(), response.getId()); // why this fails
        assertEquals(address.getUserId(), response.getUserId());

        verify(addressRepository, times(1)).existsByUserIdAndAddressType(userId, createRequest.addressType());
        verify(addressRepository, times(1)).save(captor.capture());

        Address savedAddress = captor.getValue();
        assertEquals(createRequest.addressType(), savedAddress.getAddressType());
        assertEquals(createRequest.street1(), savedAddress.getStreet1());
        assertEquals(createRequest.street2(), savedAddress.getStreet2());
        assertEquals(createRequest.city(), savedAddress.getCity());
        assertEquals(createRequest.county(), savedAddress.getCounty());
        assertEquals(createRequest.country(), savedAddress.getCountry());
        assertEquals(createRequest.postcode(), savedAddress.getPostcode());
    }

    @Test
    void testCreateAddress_SameAddressTypeAlreadyExists() {
        // Arrange
        when(addressRepository.existsByUserIdAndAddressType(userId, createRequest.addressType())).thenReturn(true);

        //Act & Assert
        assertThrows(AddressTypeExistsException.class, () -> addressService.createAddress(userId, createRequest));

        // Assert
        verify(addressRepository, times(1)).existsByUserIdAndAddressType(userId, createRequest.addressType());
        verify(addressRepository, never()).save(any(Address.class));
    }

    // ------------------------------------

    @Test
    void testGetAllAddresses_Success() {
        // Arrange
        when(addressRepository.findAllByUserId(userId)).thenReturn(List.of(address, address2));

        // Act
        List<Address> response = addressService.getAllAddresses(userId);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(address.getId(), response.get(0).getId());
        assertEquals(address.getUserId(), response.get(0).getUserId());
        assertEquals(address2.getId(), response.get(1).getId());
        assertEquals(address2.getUserId(), response.get(1).getUserId());

        verify(addressRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void testGetAllAddresses_NonExistingUser() {
        // Arrange
        UUID nonExistingUserId = UUID.randomUUID();
        when(addressRepository.findAllByUserId(nonExistingUserId)).thenReturn(List.of());

        // Act
        List<Address> response = addressService.getAllAddresses(nonExistingUserId);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.size());

        verify(addressRepository, times(1)).findAllByUserId(nonExistingUserId);
    }

    // ------------------------------------

    @Test
    void testGetAddress_Success() {
        // Arrange
        when(addressRepository.findByIdAndUserId(addressId, userId)).thenReturn(Optional.of(address));

        // Act
        Address response = addressService.getAddress(userId, addressId);

        // Assert
        assertNotNull(response);
        assertEquals(address.getId(), response.getId());
        assertEquals(address.getAddressType(), response.getAddressType());
        assertEquals(address.getStreet1(), response.getStreet1());
        assertEquals(address.getStreet2(), response.getStreet2());
        assertEquals(address.getCity(), response.getCity());
        assertEquals(address.getCounty(), response.getCounty());
        assertEquals(address.getCountry(), response.getCountry());
        assertEquals(address.getPostcode(), response.getPostcode());

        verify(addressRepository, times(1)).findByIdAndUserId(addressId, userId);
    }

    @Test
    void testGetAddress_NonExistentUser() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(addressRepository.findByIdAndUserId(addressId, nonExistentId)).thenReturn(Optional.empty());

        // Assert & Act
        assertThrows(ResourceNotFoundException.class, () -> addressService.getAddress(nonExistentId, addressId));

        verify(addressRepository, times(1)).findByIdAndUserId(addressId, nonExistentId);
    }

    // ------------------------------------

    @Test
    void testUpdateUser_Success() {
        // Arrange
        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);

        when(addressRepository.findByIdAndUserId(addressId, userId)).thenReturn(Optional.of(address));
        when(addressRepository.existsByUserIdAndAddressTypeAndIdNot(userId, updateRequest.addressType(), addressId))
                .thenReturn(false);
        when(addressRepository.save(captor.capture())).thenReturn(address);

        // Act
        Address response = addressService.updateAddress(userId, addressId, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(addressId, response.getId());
        assertEquals(updateRequest.street1(), response.getStreet1());
        assertEquals(updateRequest.city(), response.getCity());
        assertEquals(updateRequest.county(), response.getCounty());
        assertEquals(updateRequest.postcode(), response.getPostcode());

        verify(addressRepository, times(1)).findByIdAndUserId(addressId, userId);
        verify(addressRepository, times(1))
                .existsByUserIdAndAddressTypeAndIdNot(userId, updateRequest.addressType(), addressId);
        verify(addressRepository, times(1)).save(captor.capture());

        Address savedAddress = captor.getValue();
        assertEquals(updateRequest.street1(), savedAddress.getStreet1());
        assertEquals(updateRequest.city(), savedAddress.getCity());
        assertEquals(updateRequest.county(), savedAddress.getCounty());
        assertEquals(updateRequest.postcode(), savedAddress.getPostcode());
    }

    @Test
    void testUpdateUser_NonExistentUser() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(addressRepository.findByIdAndUserId(addressId, nonExistentId)).thenReturn(Optional.empty());

        // Assert & Act
        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.updateAddress(nonExistentId, addressId, updateRequest);
        });

        // Assert
        verify(addressRepository, times(1)).findByIdAndUserId(addressId, nonExistentId);
        verify(addressRepository, never())
                .existsByUserIdAndAddressTypeAndIdNot(any(UUID.class), any(AddressType.class), any(UUID.class));
        verify(addressRepository, never()).save(any(Address.class));
    }

    // ------------------------------------

    @Test
    void testDeleteAddress_Success() {
        // Arrange
        when(addressRepository.findByIdAndUserId(addressId, userId)).thenReturn(Optional.of(address));

        // Act
        addressService.deleteAddress(userId, addressId);

        // Assert
        verify(addressRepository, times(1)).findByIdAndUserId(addressId, userId);
        verify(addressRepository, times(1)).delete(address);
    }

    @Test
    void testDeleteAddress_NonExistentAddress() {
        // Arrange
        when(addressRepository.findByIdAndUserId(addressId, userId)).thenReturn(Optional.empty());

        // Assert & Act
        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.deleteAddress(userId, addressId);
        });

        // Assert
        verify(addressRepository, times(1)).findByIdAndUserId(addressId, userId);
        verify(addressRepository, never()).delete(any(Address.class));
    }

    // ------------------------------------

    @Test
    void testDeleteUserAddresses_Success() {
        // Arrange
        doNothing().when(addressRepository).deleteAllByUserId(userId);

        // Act
        addressService.deleteUserAddresses(userId);

        // Assert
        verify(addressRepository, times(1)).deleteAllByUserId(userId);
    }

}
