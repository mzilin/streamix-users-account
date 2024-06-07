package com.mariuszilinskas.vsp.userservice.converter;

import com.mariuszilinskas.vsp.userservice.enums.UserAuthority;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserAuthorityConverter extends EnumConverter<UserAuthority> {

    public UserAuthorityConverter() {
        super(UserAuthority.class);
    }

}
