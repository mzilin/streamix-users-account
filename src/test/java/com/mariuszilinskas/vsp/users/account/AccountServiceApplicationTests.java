package com.mariuszilinskas.vsp.users.account;

import com.mariuszilinskas.vsp.users.account.client.IdentityFeignClient;
import com.mariuszilinskas.vsp.users.account.config.FeignConfig;
import com.mariuszilinskas.vsp.users.account.config.RabbitMQConfig;
import com.mariuszilinskas.vsp.users.account.consumer.RabbitMQConsumer;
import com.mariuszilinskas.vsp.users.account.controller.UserAdminController;
import com.mariuszilinskas.vsp.users.account.controller.UserController;
import com.mariuszilinskas.vsp.users.account.producer.RabbitMQProducer;
import com.mariuszilinskas.vsp.users.account.repository.UserRepository;
import com.mariuszilinskas.vsp.users.account.service.UserAdminServiceImp;
import com.mariuszilinskas.vsp.users.account.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for the Spring application context and bean configuration in the UserService application.
 */
@SpringBootTest
class AccountServiceApplicationTests {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserAdminServiceImp userAdminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private UserAdminController userAdminController;

    @Autowired
    private IdentityFeignClient identityFeignClient;

    @Autowired
    private FeignConfig feignConfig;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @Autowired
    private RabbitMQConsumer rabbitMQConsumer;

    @Test
    void contextLoads() {
    }

    @Test
    void userServiceBeanLoads() {
        assertNotNull(userService, "User Service should have been auto-wired by Spring Context");
    }

    @Test
    void userAdminServiceBeanLoads() {
        assertNotNull(userAdminService, "User Admin Service should have been auto-wired by Spring Context");
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
    void userAdminControllerBeanLoads() {
        assertNotNull(userAdminController, "User Admin Controller should have been auto-wired by Spring Context");
    }

    @Test
    void identityFeignClientBeanLoads() {
        assertNotNull(identityFeignClient, "Identity Feign Client should have been auto-wired by Spring Context");
    }

    @Test
    void feignConfigBeanLoads() {
        assertNotNull(feignConfig, "Feign Config should have been auto-wired by Spring Context");
    }

    @Test
    void rabbitMQConfigBeanLoads() {
        assertNotNull(rabbitMQConfig, "RabbitMQ Config should have been auto-wired by Spring Context");
    }

    @Test
    void rabbitMQProducerBeanLoads() {
        assertNotNull(rabbitMQProducer, "RabbitMQ Producer should have been auto-wired by Spring Context");
    }

    @Test
    void rabbitMQConsumerBeanLoads() {
        assertNotNull(rabbitMQConsumer, "RabbitMQ Consumer should have been auto-wired by Spring Context");
    }

}
