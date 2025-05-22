package com.mariuszilinskas.vsp.users.account.util;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;

import java.util.Collections;

public abstract class TestUtils {

    private TestUtils() {
        // Private constructor to prevent instantiation
    }

    public static FeignException createFeignException() {
        Request feignRequest = Request.create(
                Request.HttpMethod.POST,
                "", // Empty string for URL as a placeholder
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        return new FeignException.NotFound("Not found", feignRequest, null, Collections.emptyMap());
    }

}
