package com.mariuszilinskas.streamix.users.account.controller;

import com.mariuszilinskas.streamix.users.account.dto.UserAdminResponse;
import com.mariuszilinskas.streamix.users.account.enums.UserAuthority;
import com.mariuszilinskas.streamix.users.account.enums.UserRole;
import com.mariuszilinskas.streamix.users.account.enums.UserStatus;
import com.mariuszilinskas.streamix.users.account.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * This class provides REST APIs for handling CRUD operations related to users
 * that are only accessible by system admins.
 *
 * @author Marius Zilinskas
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserAdminResponse>> getUsers(){
        List<UserAdminResponse> response = userAdminService.getUsers();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{userId}/role/{userRole}")
    public ResponseEntity<Void> grantUserRole(
            @PathVariable UUID userId,
            @PathVariable UserRole userRole
    ){
        userAdminService.grantUserRole(userId, userRole);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{userId}/role/{userRole}")
    public ResponseEntity<Void> removeUserRole(
            @PathVariable UUID userId,
            @PathVariable UserRole userRole
    ){
        userAdminService.removeUserRole(userId, userRole);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{userId}/authority/{authority}")
    public ResponseEntity<Void> grantUserAuthority(
            @PathVariable UUID userId,
            @PathVariable UserAuthority authority
    ){
        userAdminService.grantUserAuthority(userId, authority);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{userId}/authority/{authority}")
    public ResponseEntity<Void> removeUserAuthority(
            @PathVariable UUID userId,
            @PathVariable UserAuthority authority
    ){
        userAdminService.removeUserAuthority(userId, authority);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{userId}/status/{status}")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable UUID userId,
            @PathVariable UserStatus status
    ){
        userAdminService.updateUserStatus(userId, status);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
