package com.mariuszilinskas.vsp.users.account.dto;

import com.mariuszilinskas.vsp.users.account.enums.UserAuthority;
import com.mariuszilinskas.vsp.users.account.enums.UserRole;
import com.mariuszilinskas.vsp.users.account.enums.UserStatus;

import java.util.List;
import java.util.UUID;

public record AuthDetailsResponse(
        UUID userId,
        List<UserRole> roles,
        List<UserAuthority> authorities,
        UserStatus status
) {}
