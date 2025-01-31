package com.example.librarymanager.service.impl;

import com.example.librarymanager.domain.entity.Role;
import com.example.librarymanager.repository.RoleRepository;
import com.example.librarymanager.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.librarymanager.constant.RoleConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;

    @Override
    public void initRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(ROLE_MANAGE_AUTHOR));
            roleRepository.save(new Role(ROLE_MANAGE_BOOK));
            roleRepository.save(new Role(ROLE_MANAGE_BOOK_DEFINITION));
            roleRepository.save(new Role(ROLE_MANAGE_BOOK_SET));
            roleRepository.save(new Role(ROLE_MANAGE_CATEGORY));
            roleRepository.save(new Role(ROLE_MANAGE_CATEGORY_GROUP));
            roleRepository.save(new Role(ROLE_MANAGE_CLASSIFICATION_SYMBOL));
            roleRepository.save(new Role(ROLE_MANAGE_IMPORT_RECEIPT));
            roleRepository.save(new Role(ROLE_MANAGE_EXPORT_RECEIPT));
            roleRepository.save(new Role(ROLE_MANAGE_LOG));
            roleRepository.save(new Role(ROLE_MANAGE_NEWS_ARTICLE));
            roleRepository.save(new Role(ROLE_MANAGE_PUBLISHER));
            roleRepository.save(new Role(ROLE_MANAGE_USER));
            roleRepository.save(new Role(ROLE_MANAGE_USER_GROUP));
            roleRepository.save(new Role(ROLE_MANAGE_SYSTEM_SETTINGS));
            roleRepository.save(new Role(ROLE_MANAGE_READER));
            roleRepository.save(new Role(ROLE_MANAGE_BORROW_RECEIPT));
            roleRepository.save(new Role(ROLE_READER));

            log.info("Initializing roles");
        }
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

}
