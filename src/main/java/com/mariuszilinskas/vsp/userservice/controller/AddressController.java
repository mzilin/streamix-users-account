package com.mariuszilinskas.vsp.userservice.controller;

import com.mariuszilinskas.vsp.userservice.dto.*;
import com.mariuszilinskas.vsp.userservice.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * This class provides REST APIs for handling CRUD operations related to user addresses.
 *
 * @author Marius Zilinskas
 */
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/{userId}")
    public ResponseEntity<AddressResponse> createAddress(
            @Valid @RequestBody UpdateAddressRequest request
    ) {
        AddressResponse response = addressService.createAddress(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<AddressResponse>> getAllAddresses(@PathVariable UUID userId) {
        List<AddressResponse> response = addressService.getAllAddresses(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{addressId}")
    public ResponseEntity<AddressResponse> getAddress(
            @PathVariable UUID userId,
            @PathVariable UUID addressId
    ) {
        AddressResponse response = addressService.getAddress(userId, addressId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{userId}/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable UUID userId,
            @PathVariable UUID addressId,
            @Valid @RequestBody UpdateAddressRequest request
    ) {
        AddressResponse response = addressService.updateAddress(userId, addressId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable UUID userId,
            @PathVariable UUID addressId
    ) {
        addressService.deleteAddress(userId, addressId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserAddresses(@PathVariable UUID userId) {
        addressService.deleteUserAddresses(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
