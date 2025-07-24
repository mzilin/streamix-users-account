package com.mariuszilinskas.vsp.users.account.mapper;

import com.mariuszilinskas.vsp.users.account.dto.*;
import com.mariuszilinskas.vsp.users.account.enums.UserRole;
import com.mariuszilinskas.vsp.users.account.enums.UserStatus;
import com.mariuszilinskas.vsp.users.account.model.User;

import java.util.List;

public class UserMapper {

    public static User mapFromCreateRequest(CreateUserRequest request) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setCountry(request.country());
        user.setRoles(List.of(UserRole.USER));
        user.setStatus(UserStatus.PENDING);
        return user;
    }

    public static UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCountry(),
                user.getStatus().name()
        );
    }

    public static UpdateEmailResponse mapToUpdateEmailResponse(User user) {
        return new UpdateEmailResponse(
                user.getId(),
                user.getEmail(),
                user.isEmailVerified()
        );
    }

    public static AuthDetailsResponse mapToAuthDetailsResponse(User user) {
        return new AuthDetailsResponse(
                user.getId(),
                user.getRoles(),
                user.getAuthorities(),
                user.getStatus()
        );
    }

    public static CredentialsRequest toCredentialsRequest(User user, String password) {
        return new CredentialsRequest(
                user.getId(),
                user.getFirstName(),
                user.getEmail(),
                password
        );
    }

    public static CreateUserDefaultProfileRequest toDefaultProfileRequest(User user) {
        return new CreateUserDefaultProfileRequest(
                user.getId(),
                user.getFirstName()
        );
    }
}
