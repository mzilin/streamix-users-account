package com.mariuszilinskas.vsp.users.account.dto;

import com.mariuszilinskas.vsp.users.account.enums.UserAuthority;
import com.mariuszilinskas.vsp.users.account.enums.UserRole;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record UserAdminResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String country,
        boolean isEmailVerified,
        String status,
        List<UserRole> roles,
        List<UserAuthority> authorities,
        ZonedDateTime createdAt,
        ZonedDateTime lastActive
) {}
