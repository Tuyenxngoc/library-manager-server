package com.example.librarymanager.service.impl;

import com.example.librarymanager.domain.entity.Role;
import com.example.librarymanager.repository.RoleRepository;
import com.example.librarymanager.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.librarymanager.constant.RoleConstant.*;

@Log4j2
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;

    @Override
    public void init() {
        log.info("Starting role initialization");

        if (roleRepository.count() != 0) {
            log.info("Roles already initialized. Skipping initialization.");
            return;
        }

        List<Role> roles = List.of(
                new Role(ROLE_MANAGE_AUTHOR),
                new Role(ROLE_MANAGE_BOOK),
                new Role(ROLE_MANAGE_BOOK_DEFINITION),
                new Role(ROLE_MANAGE_BOOK_SET),
                new Role(ROLE_MANAGE_CATEGORY),
                new Role(ROLE_MANAGE_CATEGORY_GROUP),
                new Role(ROLE_MANAGE_CLASSIFICATION_SYMBOL),
                new Role(ROLE_MANAGE_IMPORT_RECEIPT),
                new Role(ROLE_MANAGE_EXPORT_RECEIPT),
                new Role(ROLE_MANAGE_LOG),
                new Role(ROLE_MANAGE_NEWS_ARTICLE),
                new Role(ROLE_MANAGE_PUBLISHER),
                new Role(ROLE_MANAGE_USER),
                new Role(ROLE_MANAGE_USER_GROUP),
                new Role(ROLE_MANAGE_SYSTEM_SETTINGS),
                new Role(ROLE_MANAGE_READER),
                new Role(ROLE_MANAGE_BORROW_RECEIPT),
                new Role(ROLE_READER)
        );

        roleRepository.saveAll(roles);
        log.info("Role initialization completed. {} roles have been added.", roles.size());
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }
}
