package com.mariuszilinskas.streamix.users.account.exception;

import com.mariuszilinskas.streamix.users.account.enums.AddressType;

public class AddressTypeExistsException extends RuntimeException {

    public AddressTypeExistsException(AddressType addressType) {
        super(String.format("The %s address is already associated with an account.", addressType));
    }

}
