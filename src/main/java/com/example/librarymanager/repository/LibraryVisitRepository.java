package com.example.librarymanager.repository;

import com.example.librarymanager.domain.entity.LibraryVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LibraryVisitRepository extends JpaRepository<LibraryVisit, Long>, JpaSpecificationExecutor<LibraryVisit> {
    LibraryVisit findTopByReaderIdAndEntryTimeBetweenOrderByEntryTimeDesc(Long readerId, LocalDateTime start, LocalDateTime end);

    List<LibraryVisit> findAllByEntryTimeBetweenAndExitTimeIsNull(LocalDateTime start, LocalDateTime end);
}
