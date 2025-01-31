package com.example.librarymanager.repository;

import com.example.librarymanager.domain.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {
    boolean existsByCode(String code);

    Optional<Author> findByIdAndActiveFlagIsTrue(Long id);
}
