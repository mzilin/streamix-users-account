package com.mariuszilinskas.vsp.users.account.dto;

import com.mariuszilinskas.vsp.users.account.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static com.mariuszilinskas.vsp.users.account.constant.RequestValidationMessages.*;

public record UpdateAddressRequest(

        @NotNull(message = "addressType " + CANNOT_BE_NULL)
        AddressType addressType,

        @NotBlank(message = "street1 " + CANNOT_BE_BLANK)
        String street1,

        String street2,

        @NotBlank(message = "city " + CANNOT_BE_BLANK)
        String city,

        String county,

        @NotBlank(message = "postcode " + CANNOT_BE_BLANK)
        String postcode,

        @NotBlank(message = "country " + CANNOT_BE_BLANK)
        String country

) {
        public UpdateAddressRequest {
                if (street1 != null) street1 = street1.trim();
                if (street2 != null) street2 = street2.trim();
                if (city != null) city = city.trim();
                if (county != null) county = county.trim();
                if (postcode != null) postcode = postcode.trim();
        }
}
