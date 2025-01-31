package com.example.librarymanager.repository;

import com.example.librarymanager.domain.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long>, JpaSpecificationExecutor<Reader> {
    Optional<Reader> findByCardNumber(String cardNumber);

    Optional<Reader> findByCardNumberAndEmail(String cardNumber, String email);

    boolean existsByCardNumber(String cardNumber);

    boolean existsByEmail(String email);

    List<Reader> findAllByIdIn(Set<Long> readerIds);
}
