package com.mariuszilinskas.vsp.userservice.controller;

import com.mariuszilinskas.vsp.userservice.enums.UserAuthority;
import com.mariuszilinskas.vsp.userservice.enums.UserRole;
import com.mariuszilinskas.vsp.userservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/{userId}/role/{userRole}")
    public ResponseEntity<Void> grantUserRole(
            @PathVariable UUID userId,
            @PathVariable UserRole userRole
    ){
        adminService.grantUserRole(userId, userRole);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{userId}/role/{userRole}")
    public ResponseEntity<Void> removeUserRole(
            @PathVariable UUID userId,
            @PathVariable UserRole userRole
    ){
        adminService.removeUserRole(userId, userRole);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{userId}/authority/{authority}")
    public ResponseEntity<Void> grantUserAuthority(
            @PathVariable UUID userId,
            @PathVariable UserAuthority authority
    ){
        adminService.grantUserAuthority(userId, authority);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{userId}/authority/{authority}")
    public ResponseEntity<Void> removeUserAuthority(
            @PathVariable UUID userId,
            @PathVariable UserAuthority authority
    ){
        adminService.removeUserAuthority(userId, authority);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{userId}/suspend")
    public ResponseEntity<Void> suspendUer(@PathVariable UUID userId){
        adminService.suspendUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
