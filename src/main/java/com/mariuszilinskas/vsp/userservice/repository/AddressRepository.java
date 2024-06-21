package com.mariuszilinskas.vsp.userservice.repository;

import com.mariuszilinskas.vsp.userservice.enums.AddressType;
import com.mariuszilinskas.vsp.userservice.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Address entities. Supports standard CRUD operations.
 *
 * @author Marius Zilinskas
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    boolean existsByUserIdAndAddressType(UUID userId, AddressType addressType);

    boolean existsByUserIdAndAddressTypeAndIdNot(UUID userId, AddressType addressType, UUID addressId);

    Optional<Address> findByIdAndUserId(UUID addressId, UUID userId);

    List<Address> findAllByUserId(UUID userId);

    void deleteAllByUserId(UUID userId);

}
