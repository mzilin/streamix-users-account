package com.mariuszilinskas.streamix.users.account.converter;

import com.mariuszilinskas.streamix.users.account.enums.UserAuthority;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserAuthorityConverter extends EnumConverter<UserAuthority> {

    public UserAuthorityConverter() {
        super(UserAuthority.class);
    }

}
