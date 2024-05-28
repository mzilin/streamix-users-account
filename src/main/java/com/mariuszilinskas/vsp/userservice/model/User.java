package com.mariuszilinskas.vsp.userservice.model;

import com.mariuszilinskas.vsp.userservice.enums.UserRole;
import com.mariuszilinskas.vsp.userservice.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * This entity describes a User within the platform. It stores personal and account-related information.
 * It also handles relationships with other entities, such as user profiles and addresses.
 *
 * @author Marius Zilinskas
 */
@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserProfile> userProfiles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Address> addresses;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "last_active", nullable = false)
    private ZonedDateTime lastActive = ZonedDateTime.now();

}
