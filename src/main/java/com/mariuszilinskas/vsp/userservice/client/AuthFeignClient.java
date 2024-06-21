package com.mariuszilinskas.vsp.userservice.client;

import com.mariuszilinskas.vsp.userservice.dto.CredentialsRequest;
import com.mariuszilinskas.vsp.userservice.dto.VerifyPasswordRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("auth")
public interface AuthFeignClient {

    @PostMapping(value = "/credentials", consumes = "application/json")
    ResponseEntity<Void> createPasswordAndSetPasscode(@RequestBody CredentialsRequest request);

    @PostMapping(value = "/password/verify", consumes = "application/json")
    ResponseEntity<Void> verifyPassword(@RequestBody VerifyPasswordRequest request);

}
