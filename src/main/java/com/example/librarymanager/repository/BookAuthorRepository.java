package com.example.librarymanager.repository;

import com.example.librarymanager.domain.entity.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {
    @Modifying
    @Transactional
    void deleteAllByBookDefinitionId(Long id);
}
