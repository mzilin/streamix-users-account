package com.mariuszilinskas.vsp.users.account.controller;

import com.mariuszilinskas.vsp.users.account.dto.*;
import com.mariuszilinskas.vsp.users.account.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * This class provides REST APIs for handling CRUD operations related to users.
 *
 * @author Marius Zilinskas
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        UserResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID userId) {
        UserResponse response = userService.getUser(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UserResponse response = userService.updateUser(userId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{userId}/email")
    public ResponseEntity<UpdateEmailResponse> updateUserEmail(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateEmailRequest request
    ) {
        UpdateEmailResponse response = userService.updateUserEmail(userId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ------------------------------------------------------

    @PatchMapping("/{userId}/verify")
    public ResponseEntity<Void> verifyUser(@PathVariable UUID userId){
        userService.verifyUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/auth-details/by-email")
    public ResponseEntity<AuthDetailsResponse> getUserAuthDetailsByEmail(
            @Valid @Email @RequestParam String email
    ){
        AuthDetailsResponse authDetails = userService.getUserAuthDetailsByEmail(email);
        return new ResponseEntity<>(authDetails, HttpStatus.OK);
    }

    @GetMapping("/auth-details/by-userid")
    public ResponseEntity<AuthDetailsResponse> getUserAuthDetailsByUserId(
            @Valid @RequestParam UUID userId
    ){
        AuthDetailsResponse authDetails = userService.getUserAuthDetailsByUserId(userId);
        return new ResponseEntity<>(authDetails, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID userId,
            @Valid @RequestBody DeleteUserRequest request
    ) {
        userService.deleteUser(userId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
