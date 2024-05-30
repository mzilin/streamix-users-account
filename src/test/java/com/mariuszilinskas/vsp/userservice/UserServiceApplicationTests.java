package com.mariuszilinskas.vsp.userservice;

import com.mariuszilinskas.vsp.userservice.client.AuthFeignClient;
import com.mariuszilinskas.vsp.userservice.controller.UserController;
import com.mariuszilinskas.vsp.userservice.repository.UserRepository;
import com.mariuszilinskas.vsp.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for the Spring application context and bean configuration in the UserService application.
 */
@SpringBootTest
class UserServiceApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private AuthFeignClient authFeignClient;

    @Test
    void contextLoads() {
    }

    @Test
    void userServiceBeanLoads() {
        assertNotNull(userService, "User Service should have been auto-wired by Spring Context");
    }

    @Test
    void userRepositoryBeanLoads() {
        assertNotNull(userRepository, "User Repository should have been auto-wired by Spring Context");
    }

    @Test
    void userControllerBeanLoads() {
        assertNotNull(userController, "User Controller should have been auto-wired by Spring Context");
    }

    @Test
    void authFeignClientBeanLoads() {
        assertNotNull(authFeignClient, "Auth Feign Client should have been auto-wired by Spring Context");
    }

}
