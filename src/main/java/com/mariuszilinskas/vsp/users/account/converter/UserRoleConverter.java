package com.mariuszilinskas.vsp.users.account.converter;

import com.mariuszilinskas.vsp.users.account.enums.UserRole;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter extends EnumConverter<UserRole> {

    public UserRoleConverter() {
        super(UserRole.class);
    }

}
