package com.mariuszilinskas.vsp.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * This entity describes a user profile within the platform.
 * Profiles are automatically removed when the associated user account is deleted.
 *
 * @author Marius Zilinskas
 */
@Entity
@Getter
@Setter
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "profile_name", nullable = false)
    private String profileName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "is_kid")
    private boolean isKid;

}
