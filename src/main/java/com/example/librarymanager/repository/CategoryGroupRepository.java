package com.example.librarymanager.repository;

import com.example.librarymanager.domain.entity.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, Long>, JpaSpecificationExecutor<CategoryGroup> {
    boolean existsByGroupName(String groupName);

    Optional<CategoryGroup> findByIdAndActiveFlagIsTrue(long id);
}
