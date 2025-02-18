package com.grid.store.repository;

import com.grid.store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailId(String emailId);

    User findByEmailIdAndPassword(String emailId, String password);

    boolean existsByEmailId(String emailId);
}
