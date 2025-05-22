package com.mariuszilinskas.vsp.users.account.exception;

import com.mariuszilinskas.vsp.users.account.enums.AddressType;

public class AddressTypeExistsException extends RuntimeException {

    public AddressTypeExistsException(AddressType addressType) {
        super(String.format("The %s address is already associated with an account.", addressType));
    }

}
