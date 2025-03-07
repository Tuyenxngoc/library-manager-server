package com.example.librarymanager.repository;

import com.example.librarymanager.domain.dto.response.statistics.PublicationResponseDto;
import com.example.librarymanager.domain.entity.BookBorrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookBorrowRepository extends JpaRepository<BookBorrow, Long>, JpaSpecificationExecutor<BookBorrow> {

    @Query(""" 
            SELECT new com.example.librarymanager.domain.dto.response.statistics.PublicationResponseDto(bd.title, COUNT(bb.id)) 
                        FROM BookBorrow bb 
                        JOIN bb.book b 
                        JOIN b.bookDefinition bd 
                        GROUP BY bd.id 
                        ORDER BY COUNT(bb.id) DESC 
                        LIMIT 5 
            """)
    List<PublicationResponseDto> findTop5ByOrderByBorrowCountDesc();

    @Query("""
                SELECT DISTINCT br.reader.id 
                FROM BookBorrow bb
                JOIN bb.borrowReceipt br
                JOIN bb.book b
                WHERE b.bookDefinition.id = :bookDefinitionId
            """)
    Set<Long> getReadersBorrowed(@Param("bookDefinitionId") Long bookDefinitionId);

    @Query("""
                SELECT DISTINCT b.bookDefinition.id 
                FROM BookBorrow bb
                JOIN bb.borrowReceipt br
                JOIN bb.book b
                WHERE br.reader.cardNumber = :cardNumber
            """)
    Set<Long> getBorrowedBookDefinitionIds(@Param("cardNumber") String cardNumber);

}
