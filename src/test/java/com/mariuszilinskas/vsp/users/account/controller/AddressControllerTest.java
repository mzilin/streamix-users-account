package com.mariuszilinskas.vsp.users.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mariuszilinskas.vsp.users.account.dto.UpdateAddressRequest;
import com.mariuszilinskas.vsp.users.account.enums.AddressType;
import com.mariuszilinskas.vsp.users.account.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.users.account.model.Address;
import com.mariuszilinskas.vsp.users.account.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(AddressController.class)
public class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID addressId;
    private Address address;
    private UpdateAddressRequest request;

    // ------------------------------------

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        addressId = UUID.randomUUID();

        address = new Address();
        address.setId(addressId);
        address.setUserId(userId);
        address.setAddressType(AddressType.BILLING);
        address.setStreet1("Street 1");

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

    // ------------------------------------

    @Test
    void testCreateAddress_Success() throws Exception {
        // Arrange
        when(addressService.createAddress(any(), any())).thenReturn(address);

        // Act & Assert
        mockMvc.perform(post("/address/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(addressId.toString()));
    }

    @Test
    void testCreateAddress_RequiredFieldsMissing() throws Exception {
        // Arrange
        var invalidRequest = new UpdateAddressRequest(null, null, null, null, null, null, null);

        // Act & Assert
        mockMvc.perform(post("/address/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.addressType").value("addressType cannot be null"))
                .andExpect(jsonPath("$.fieldErrors.street1").value("street1 cannot be blank"))
                .andExpect(jsonPath("$.fieldErrors.city").value("city cannot be blank"))
                .andExpect(jsonPath("$.fieldErrors.postcode").value("postcode cannot be blank"))
                .andExpect(jsonPath("$.fieldErrors.country").value("country cannot be blank"));
    }

    @Test
    void testGetAllAddresses_Success() throws Exception {
        // Arrange
        when(addressService.getAllAddresses(userId)).thenReturn(List.of(address));

        // Act & Assert
        mockMvc.perform(get("/address/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(addressId.toString()));
    }

    @Test
    void testGetAllAddresses_EmptyList() throws Exception {
        // Arrange
        when(addressService.getAllAddresses(userId)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/address/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetAddressById_Success() throws Exception {
        // Arrange
        when(addressService.getAddress(userId, addressId)).thenReturn(address);

        // Act & Assert
        mockMvc.perform(get("/address/{userId}/{addressId}", userId, addressId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressId.toString()));
    }

    @Test
    void testGetAddressById_NotFound() throws Exception {
        // Arrange
        when(addressService.getAddress(userId, addressId))
                .thenThrow(new ResourceNotFoundException(Address.class, "id", addressId));

        // Act & Assert
        mockMvc.perform(get("/address/{userId}/{addressId}", userId, addressId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateAddress_Success() throws Exception {
        // Arrange
        when(addressService.updateAddress(any(), any(), any())).thenReturn(address);

        // Act & Assert
        mockMvc.perform(put("/address/{userId}/{addressId}", userId, addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressId.toString()));
    }

    @Test
    void testUpdateAddress_NotFound() throws Exception {
        // Arrange
        when(addressService.updateAddress(eq(userId), eq(addressId), any()))
                .thenThrow(new ResourceNotFoundException(Address.class, "id", addressId));

        // Act & Assert
        mockMvc.perform(put("/address/{userId}/{addressId}", userId, addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateAddress_RequiredFieldsMissing() throws Exception {
        // Arrange
        var invalidRequest = new UpdateAddressRequest(null, null, null, null, null, null, null);

        // Act & Assert
        mockMvc.perform(post("/address/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.addressType").value("addressType cannot be null"))
                .andExpect(jsonPath("$.fieldErrors.street1").value("street1 cannot be blank"))
                .andExpect(jsonPath("$.fieldErrors.city").value("city cannot be blank"))
                .andExpect(jsonPath("$.fieldErrors.postcode").value("postcode cannot be blank"))
                .andExpect(jsonPath("$.fieldErrors.country").value("country cannot be blank"));
    }

    @Test
    void testDeleteAddress_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/address/{userId}/{addressId}", userId, addressId))
                .andExpect(status().isNoContent());
        verify(addressService).deleteAddress(userId, addressId);
    }

    @Test
    void testDeleteAddress_NotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException(Address.class, "id", addressId))
                .when(addressService).deleteAddress(userId, addressId);

        // Act & Assert
        mockMvc.perform(delete("/address/{userId}/{addressId}", userId, addressId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUserAddresses_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/address/{userId}", userId))
                .andExpect(status().isNoContent());
        verify(addressService).deleteUserAddresses(userId);
    }

}
