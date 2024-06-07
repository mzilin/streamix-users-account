package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.enums.UserAuthority;
import com.mariuszilinskas.vsp.userservice.enums.UserRole;
import com.mariuszilinskas.vsp.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service implementation for managing user accounts, accessible only by system admins.
 * This service handles user roles, authorities, and suspension.
 *
 * @author Marius Zilinskas
 */
@Service
@RequiredArgsConstructor
public class UserAdminServiceImp implements UserAdminService {

    private static final Logger logger = LoggerFactory.getLogger(UserAdminServiceImp.class);
    private final UserRepository userRepository;

    @Override
    public void grantUserRole(UUID userId, UserRole userRole) {

    }

    @Override
    public void removeUserRole(UUID userId, UserRole userRole) {

    }

    @Override
    public void grantUserAuthority(UUID userId, UserAuthority authority) {

    }

    @Override
    public void removeUserAuthority(UUID userId, UserAuthority authority) {

    }

    @Override
    public void suspendUser(UUID userId) {

    }

}
