package com.mariuszilinskas.vsp.userservice.controller;

import com.mariuszilinskas.vsp.userservice.dto.*;
import com.mariuszilinskas.vsp.userservice.enums.UserRole;
import com.mariuszilinskas.vsp.userservice.service.UserService;
import jakarta.validation.Valid;
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
@RequestMapping()
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
    public ResponseEntity<UserResponse> getUser(
            @PathVariable UUID userId
    ) {
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

//    @PostMapping("/{userId}/address")
//    public

    // TODO: add address
    // TODO: update address
    // TODO: delete address

    // TODO: add profile
    // TODO: get user profiles???
    // TODO: update profile
    // TODO: delete profile

    // TODO: create, get, update watchlist

    // TODO: update, get viewing history

    // ------------------------------------------------------

    @PatchMapping("/{userId}/verify-email")
    public ResponseEntity<Void> verifyUserEmail(
            @PathVariable UUID userId
    ){
        userService.verifyUserEmail(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{userId}/role")
    public ResponseEntity<UserRole> getUserRole(
            @PathVariable UUID userId
    ){
        UserRole userRole = userService.getUserRole(userId);
        return new ResponseEntity<>(userRole, HttpStatus.OK);
    }

    @GetMapping("/id-by-email")
    public ResponseEntity<UUID> getUserIdByEmail(
            @Valid @RequestBody UserIdRequest request
    ){
        UUID userId = userService.getUserIdByEmail(request);
        return new ResponseEntity<>(userId, HttpStatus.OK);
    }

}
