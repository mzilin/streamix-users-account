package com.mariuszilinskas.vsp.users.account.dto;

import com.mariuszilinskas.vsp.users.account.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAddressRequest(

        @NotNull(message = "addressType cannot be null")
        AddressType addressType,

        @NotBlank(message = "street1 cannot be blank")
        String street1,

        String street2,

        @NotBlank(message = "city cannot be blank")
        String city,

        String county,

        @NotBlank(message = "postcode cannot be blank")
        String postcode,

        @NotBlank(message = "country cannot be blank")
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
