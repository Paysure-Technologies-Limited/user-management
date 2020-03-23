package com.bizzdesk.group.user.management.repository;

import com.bizzdesk.group.user.management.entities.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, String> {

    List<Role> findAll();
}
