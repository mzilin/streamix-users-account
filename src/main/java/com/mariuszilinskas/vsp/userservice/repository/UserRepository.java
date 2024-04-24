package com.mariuszilinskas.vsp.userservice.repository;

import com.mariuszilinskas.vsp.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing User entities. Supports standard CRUD operations.
 *
 * @author Marius Zilinskas
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

}
