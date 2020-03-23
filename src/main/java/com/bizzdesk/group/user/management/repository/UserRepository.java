package com.bizzdesk.group.user.management.repository;

import com.bizzdesk.group.user.management.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByEmailAddress(String emailAddress);
    Optional<User> findByUserIdAndVerificationCode(String userId, Long verificationCode);
}
