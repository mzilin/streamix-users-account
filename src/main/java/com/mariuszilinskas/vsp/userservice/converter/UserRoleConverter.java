package com.mariuszilinskas.vsp.userservice.converter;

import com.mariuszilinskas.vsp.userservice.enums.UserRole;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter extends EnumConverter<UserRole> {

    public UserRoleConverter() {
        super(UserRole.class);
    }

}
