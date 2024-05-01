package com.mariuszilinskas.vsp.userservice.client;

import com.mariuszilinskas.vsp.userservice.dto.CreateCredentialsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("auth")
public interface AuthFeignClient {

    @PostMapping(value = "createPasswordAndSetPasscode", consumes = "application/json")
    ResponseEntity<Void> createPasswordAndSetPasscode(@RequestBody CreateCredentialsRequest request);

}
