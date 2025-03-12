package com.example.librarymanager.repository;

import com.example.librarymanager.domain.dto.response.bookdefinition.BookDefinitionResponseDto;
import com.example.librarymanager.domain.dto.response.bookdefinition.BookForReaderResponseDto;
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
    boolean existsByBookNumber(String bookNumber);

    Optional<BookDefinition> findByIdAndActiveFlagIsTrue(Long id);

    @Query("SELECT DISTINCT bd.id FROM BookDefinition bd JOIN Book b ON b.bookDefinition = bd")
    Set<Long> findAllBookDefinitionIdsWithBook();

    @Query("""
            SELECT new com.example.librarymanager.domain.dto.response.bookdefinition.BookDefinitionResponseDto(b) 
                        FROM BookDefinition b 
                        WHERE b.id IN :ids
            """)
    List<BookDefinitionResponseDto> findBookDefinitionsByIds(@Param("ids") Set<Long> ids);

    @Query("SELECT new com.example.librarymanager.domain.dto.response.bookdefinition.BookForReaderResponseDto(bd) FROM BookDefinition bd WHERE bd.id IN :ids")
    List<BookForReaderResponseDto> findByIdIn(@Param("ids") List<Long> ids);

}
