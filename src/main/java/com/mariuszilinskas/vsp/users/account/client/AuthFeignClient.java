package com.mariuszilinskas.vsp.users.account.client;

import com.mariuszilinskas.vsp.users.account.dto.VerifyPasswordRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("auth")
public interface AuthFeignClient {

    @PutMapping(value = "/password/verify", consumes = "application/json")
    ResponseEntity<Void> verifyPassword(@RequestBody VerifyPasswordRequest request);

}
