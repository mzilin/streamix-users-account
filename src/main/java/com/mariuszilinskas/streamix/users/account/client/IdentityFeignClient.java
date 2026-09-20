package com.mariuszilinskas.streamix.users.account.client;

import com.mariuszilinskas.streamix.users.account.dto.SetupCredentialsRequest;
import com.mariuszilinskas.streamix.users.account.dto.VerifyPasswordRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("auth-identity")
public interface IdentityFeignClient {

    @PostMapping(value = "/credentials/setup", consumes = "application/json")
    ResponseEntity<Void> setupCredentials(@RequestBody SetupCredentialsRequest request);

    @PutMapping(value = "/password/verify", consumes = "application/json")
    ResponseEntity<Void> verifyPassword(@RequestBody VerifyPasswordRequest request);

}
