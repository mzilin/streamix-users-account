package com.mariuszilinskas.vsp.userservice.controller;

import com.mariuszilinskas.vsp.userservice.dto.*;
import com.mariuszilinskas.vsp.userservice.model.Address;
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
    public ResponseEntity<Address> createAddress(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateAddressRequest request
    ) {
        Address response = addressService.createAddress(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Address>> getAllAddresses(@PathVariable UUID userId) {
        List<Address> response = addressService.getAllAddresses(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{addressId}")
    public ResponseEntity<Address> getAddress(
            @PathVariable UUID userId,
            @PathVariable UUID addressId
    ) {
        Address response = addressService.getAddress(userId, addressId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{userId}/{addressId}")
    public ResponseEntity<Address> updateAddress(
            @PathVariable UUID userId,
            @PathVariable UUID addressId,
            @Valid @RequestBody UpdateAddressRequest request
    ) {
        Address response = addressService.updateAddress(userId, addressId, request);
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
