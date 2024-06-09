package com.mariuszilinskas.vsp.userservice.model;

import com.mariuszilinskas.vsp.userservice.enums.AddressType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * This entity describes the user's addresses within the platform, including Billing and Shipping types.
 * Each address is automatically removed when the associated user account is deleted.
 *
 * @author Marius Zilinskas
 */
@Entity
@Getter
@Setter
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false)
    private AddressType addressType;

    @Column(name = "street_1", nullable = false)
    private String street1;

    @Column(name = "street_2")
    private String street2;

    @Column(nullable = false)
    private String city;

    @Column
    private String county;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String postcode;

}
