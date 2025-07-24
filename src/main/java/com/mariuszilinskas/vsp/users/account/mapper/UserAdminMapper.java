package com.mariuszilinskas.vsp.users.account.mapper;

import com.mariuszilinskas.vsp.users.account.dto.UserAdminResponse;
import com.mariuszilinskas.vsp.users.account.model.User;

public class UserAdminMapper {

    public static UserAdminResponse mapToUserAdminResponse(User user) {
        return new UserAdminResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCountry(),
                user.isEmailVerified(),
                user.getStatus().name(),
                user.getRoles(),
                user.getAuthorities(),
                user.getCreatedAt(),
                user.getLastActive()
        );
    }

}
