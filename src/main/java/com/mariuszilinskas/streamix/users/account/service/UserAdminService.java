package com.mariuszilinskas.streamix.users.account.service;

import com.mariuszilinskas.streamix.users.account.dto.UserAdminResponse;
import com.mariuszilinskas.streamix.users.account.enums.UserAuthority;
import com.mariuszilinskas.streamix.users.account.enums.UserRole;
import com.mariuszilinskas.streamix.users.account.enums.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserAdminService {

    List<UserAdminResponse> getUsers();

    void grantUserRole(UUID userId, UserRole userRole);

    void removeUserRole(UUID userId, UserRole userRole);

    void grantUserAuthority(UUID userId, UserAuthority authority);

    void removeUserAuthority(UUID userId, UserAuthority authority);

    void updateUserStatus(UUID userId, UserStatus status);

}
