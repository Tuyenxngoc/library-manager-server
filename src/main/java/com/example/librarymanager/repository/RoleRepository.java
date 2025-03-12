package com.example.librarymanager.repository;

import com.example.librarymanager.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Byte> {

    Role findByCode(String code);

}
