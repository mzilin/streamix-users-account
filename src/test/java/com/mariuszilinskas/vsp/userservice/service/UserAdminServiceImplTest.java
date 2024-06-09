package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.dto.UserResponse;
import com.mariuszilinskas.vsp.userservice.enums.UserAuthority;
import com.mariuszilinskas.vsp.userservice.enums.UserRole;
import com.mariuszilinskas.vsp.userservice.enums.UserStatus;
import com.mariuszilinskas.vsp.userservice.model.User;
import com.mariuszilinskas.vsp.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAdminServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserAdminServiceImp userAdminService;

    private final UUID userId = UUID.randomUUID();
    private final UUID user2Id = UUID.randomUUID();
    private final User user = new User();
    private final User user2 = new User();

    // ------------------------------------

    @BeforeEach
    void setUp() {
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setCountry("United Kingdom");
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(List.of(UserRole.USER));
        user.setAuthorities(List.of());

        user2.setId(user2Id);
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setEmail("jane@example.com");
        user2.setCountry("United Kingdom");
        user2.setStatus(UserStatus.ACTIVE);
        user2.setRoles(List.of(UserRole.USER));
        user2.setAuthorities(List.of());
    }

    // ------------------------------------

    @Test
    void testGetUsers() {
        // Arrange
        List<User> users = List.of(user, user2);

        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserResponse> userResponses = userAdminService.getUsers();

        // Assert
        assertNotNull(userResponses);
        assertEquals(2, userResponses.size());
        assertEquals(user.getId(), userResponses.get(0).id());
        assertEquals(user.getFirstName(), userResponses.get(0).firstName());
        assertEquals(user2.getId(), userResponses.get(1).id());
        assertEquals(user2.getFirstName(), userResponses.get(1).firstName());

        verify(userRepository, times(1)).findAll();
    }

    // ------------------------------------

    @Test
    void testGrantUserRole_RoleIsNotPresent() {
        // Arrange
        user.setRoles(new ArrayList<>(List.of(UserRole.USER)));
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(captor.capture())).thenReturn(user);

        // Act
        userAdminService.grantUserRole(userId, UserRole.ADMIN);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        assertTrue(savedUser.getRoles().contains(UserRole.ADMIN));
    }

    @Test
    void testGrantUserRole_RoleIsPresent() {
        // Arrange
        user.setRoles(new ArrayList<>(List.of(UserRole.USER, UserRole.ADMIN)));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userAdminService.grantUserRole(userId, UserRole.ADMIN);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(user);
    }

    // ------------------------------------

    @Test
    void testRemoveUserRole_RoleIsPresent() {
        // Arrange
        user.setRoles(new ArrayList<>(List.of(UserRole.USER, UserRole.ADMIN)));
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(captor.capture())).thenReturn(user);

        // Act
        userAdminService.removeUserRole(userId, UserRole.ADMIN);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        assertFalse(savedUser.getRoles().contains(UserRole.ADMIN));
    }

    @Test
    void testRemoveUserRole_RoleIsNotPresent() {
        // Arrange
        user.setRoles(new ArrayList<>(List.of(UserRole.USER)));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userAdminService.removeUserRole(userId, UserRole.ADMIN);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(user);
    }

    // ------------------------------------

    @Test
    void testGrantUserAuthority_AuthorityIsNotPresent() {
        // Arrange
        user.setAuthorities(List.of());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(captor.capture())).thenReturn(user);

        // Act
        userAdminService.grantUserAuthority(userId, UserAuthority.MANAGE_SETTINGS);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        assertTrue(savedUser.getAuthorities().contains(UserAuthority.MANAGE_SETTINGS));
    }

    @Test
    void testGrantUserAuthority_AuthorityIsPresent() {
        // Arrange
        user.setAuthorities(new ArrayList<>(List.of(UserAuthority.MANAGE_SETTINGS)));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userAdminService.grantUserAuthority(userId, UserAuthority.MANAGE_SETTINGS);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(user);
    }

    // ------------------------------------

    @Test
    void testRemoveUserAuthority_AuthorityIsPresent() {
        // Arrange
        user.setAuthorities(new ArrayList<>(List.of(UserAuthority.MANAGE_SETTINGS)));
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(captor.capture())).thenReturn(user);

        // Act
        userAdminService.removeUserAuthority(userId, UserAuthority.MANAGE_SETTINGS);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        assertFalse(savedUser.getAuthorities().contains(UserAuthority.MANAGE_SETTINGS));
    }

    @Test
    void testRemoveUserAuthority_AuthorityIsNotPresent() {
        // Arrange
        user.setAuthorities(List.of());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userAdminService.removeUserAuthority(userId, UserAuthority.MANAGE_SETTINGS);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(user);
    }

    // ------------------------------------

    @Test
    void testUpdateUserStatus_Suspend() {
        // Arrange
        user.setStatus(UserStatus.ACTIVE);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(captor.capture())).thenReturn(user);

        // Act
        userAdminService.updateUserStatus(userId, UserStatus.SUSPENDED);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals(UserStatus.SUSPENDED, savedUser.getStatus());
    }

    @Test
    void testUpdateUserStatus_Reactivate() {
        // Arrange
        user.setStatus(UserStatus.SUSPENDED);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(captor.capture())).thenReturn(user);

        // Act
        userAdminService.updateUserStatus(userId, UserStatus.ACTIVE);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals(UserStatus.ACTIVE, savedUser.getStatus());
    }

}
