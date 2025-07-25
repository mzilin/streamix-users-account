package com.mariuszilinskas.vsp.users.account.client;

import com.mariuszilinskas.vsp.users.account.dto.CredentialsRequest;
import com.mariuszilinskas.vsp.users.account.dto.VerifyPasswordRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("auth-identity")
public interface IdentityFeignClient {

    @PutMapping(value = "/credentials", consumes = "application/json")
    ResponseEntity<Void> createCredentials(@RequestBody CredentialsRequest request);

    @PutMapping(value = "/password/verify", consumes = "application/json")
    ResponseEntity<Void> verifyPassword(@RequestBody VerifyPasswordRequest request);

}
