package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.enums.UserAuthority;
import com.mariuszilinskas.vsp.userservice.enums.UserRole;
import com.mariuszilinskas.vsp.userservice.enums.UserStatus;

import java.util.UUID;

public interface UserAdminService {

    void grantUserRole(UUID userId, UserRole userRole);

    void removeUserRole(UUID userId, UserRole userRole);

    void grantUserAuthority(UUID userId, UserAuthority authority);

    void removeUserAuthority(UUID userId, UserAuthority authority);

    void updateUserStatus(UUID userId, UserStatus status);

}
