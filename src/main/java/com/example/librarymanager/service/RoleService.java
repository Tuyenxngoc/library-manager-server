package com.example.librarymanager.service;

import com.example.librarymanager.domain.entity.Role;

import java.util.List;

public interface RoleService {

    void initRoles();

    List<Role> getRoles();

}