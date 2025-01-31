package com.example.librarymanager.repository;

import com.example.librarymanager.domain.dto.response.bookdefinition.BookDefinitionResponseDto;
import com.example.librarymanager.domain.entity.BookDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookDefinitionRepository extends JpaRepository<BookDefinition, Long>, JpaSpecificationExecutor<BookDefinition> {
    boolean existsByBookCode(String bookCode);

    Optional<BookDefinition> findByIdAndActiveFlagIsTrue(Long id);

    @Query("SELECT new com.example.librarymanager.domain.dto.response.bookdefinition.BookDefinitionResponseDto(b) " +
            "FROM BookDefinition b " +
            "WHERE b.id IN :ids")
    List<BookDefinitionResponseDto> findBookDefinitionsByIds(@Param("ids") Set<Long> ids);
}
