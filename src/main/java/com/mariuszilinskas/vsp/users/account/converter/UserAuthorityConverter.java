package com.mariuszilinskas.vsp.users.account.converter;

import com.mariuszilinskas.vsp.users.account.enums.UserAuthority;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserAuthorityConverter extends EnumConverter<UserAuthority> {

    public UserAuthorityConverter() {
        super(UserAuthority.class);
    }

}
