package com.mariuszilinskas.vsp.users.account.service;

import com.mariuszilinskas.vsp.users.account.dto.UpdateAddressRequest;
import com.mariuszilinskas.vsp.users.account.enums.AddressType;
import com.mariuszilinskas.vsp.users.account.exception.AddressTypeExistsException;
import com.mariuszilinskas.vsp.users.account.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.users.account.model.Address;
import com.mariuszilinskas.vsp.users.account.repository.AddressRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public Address createAddress(UUID userId, UpdateAddressRequest request) {
        logger.info("Creating new Address for User [userId: '{}']", userId);
        checkTheAddressTypeExists(userId, request.addressType());
        return populateNewUserWithRequestData(userId, request);
    }

    private void checkTheAddressTypeExists(UUID userId, AddressType addressType) {
        if (addressRepository.existsByUserIdAndAddressType(userId, addressType))
            throw new AddressTypeExistsException(addressType);
    }

    private Address populateNewUserWithRequestData(UUID userId, UpdateAddressRequest request) {
        Address address = new Address();
        address.setUserId(userId);
        return applyEmailUpdate(address, request);
    }

    @Override
    public List<Address> getAllAddresses(UUID userId) {
        logger.info("Getting all Addresses for User [userId: '{}']", userId);
        return addressRepository.findAllByUserId(userId);
    }

    @Override
    public Address getAddress(UUID userId, UUID addressId) {
        logger.info("Getting Address [id: '{}'] for User [userId: '{}']", addressId, userId);
        return findAddressByIdAndUserId(addressId, userId);
    }

    @Override
    @Transactional
    public Address updateAddress(UUID userId, UUID addressId, UpdateAddressRequest request) {
        logger.info("Updating Address [id: '{addressId}] for User [userId: '{}']", userId);

        Address address = findAddressByIdAndUserId(addressId, userId);
        checkTheAddressTypeExists(userId, request.addressType(), addressId);
        return applyEmailUpdate(address, request);
    }

    private void checkTheAddressTypeExists(UUID userId, AddressType addressType, UUID addressId) {
        if (addressRepository.existsByUserIdAndAddressTypeAndIdNot(userId, addressType, addressId))
            throw new AddressTypeExistsException(addressType);
    }

    private Address applyEmailUpdate(Address address, UpdateAddressRequest request) {
        address.setAddressType(request.addressType());
        address.setStreet1(request.street1());
        address.setStreet2(request.street2());
        address.setCity(request.city());
        address.setCounty(request.county());
        address.setCountry(request.country());
        address.setPostcode(request.postcode());
        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        logger.info("Deleting Address [id: '{addressId}] for User [userId: '{}']", userId);
        Address address = findAddressByIdAndUserId(addressId, userId);
        addressRepository.delete(address);
    }

    private Address findAddressByIdAndUserId(UUID addressId, UUID userId) {
        return addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(Address.class, "id", addressId));
    }

    @Override
    @Transactional
    public void deleteUserAddresses(UUID userId) {
        logger.info("Deleting all Addresses for User [userId: '{}']", userId);
        addressRepository.deleteAllByUserId(userId);
    }

}
