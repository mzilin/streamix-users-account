package com.mariuszilinskas.vsp.userservice.dto;

import com.mariuszilinskas.vsp.userservice.enums.UserAuthority;
import com.mariuszilinskas.vsp.userservice.enums.UserRole;
import com.mariuszilinskas.vsp.userservice.enums.UserStatus;

import java.util.List;
import java.util.UUID;

public record AuthDetailsResponse(
        UUID userId,
        List<UserRole> roles,
        List<UserAuthority> authorities,
        UserStatus status
) {}
