package com.example.librarymanager.repository;

import com.example.librarymanager.domain.dto.response.statistics.PublicationResponseDto;
import com.example.librarymanager.domain.entity.BookBorrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookBorrowRepository extends JpaRepository<BookBorrow, Long>, JpaSpecificationExecutor<BookBorrow> {

    @Query("SELECT new com.example.librarymanager.domain.dto.response.statistics.PublicationResponseDto(bd.title, COUNT(bb.id)) " +
            "FROM BookBorrow bb " +
            "INNER JOIN bb.book b " +
            "INNER JOIN b.bookDefinition bd " +
            "GROUP BY bd.id " +
            "order by COUNT(bb.id) DESC " +
            "LIMIT 5")
    List<PublicationResponseDto> findTop5ByOrderByBorrowCountDesc();

}
