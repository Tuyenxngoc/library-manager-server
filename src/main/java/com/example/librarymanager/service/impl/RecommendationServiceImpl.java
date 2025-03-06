package com.example.librarymanager.service.impl;

import com.example.librarymanager.domain.dto.response.bookdefinition.BookForReaderResponseDto;
import com.example.librarymanager.repository.BookBorrowRepository;
import com.example.librarymanager.repository.BookDefinitionRepository;
import com.example.librarymanager.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final BookDefinitionRepository bookDefinitionRepository;

    private final BookBorrowRepository bookBorrowRepository;

    /**
     * ∣A∪B∣
     * J(A,B)= -----
     * ∣A∩B∣
     */
    private double jaccardSimilarity(Long bookA, Long bookB) {
        Set<Long> usersA = bookBorrowRepository.getReadersBorrowed(bookA);
        Set<Long> usersB = bookBorrowRepository.getReadersBorrowed(bookB);

        Set<Long> intersection = new HashSet<>(usersA);
        intersection.retainAll(usersB);// Giao của hai tập người dùng

        Set<Long> union = new HashSet<>(usersA);
        union.addAll(usersB); // Hợp của hai tập người dùng

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    @Override
    public List<BookForReaderResponseDto> recommendBooks(String cardNumber, int topN) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> borrowedBooks = bookBorrowRepository.getBorrowedBookDefinitions(cardNumber);
        List<Long> allBooks = bookDefinitionRepository.findAllBookDefinitionIdsWithBook();

        Map<Long, Double> similarityScores = new HashMap<>();

        for (Long book : allBooks) {
            if (!borrowedBooks.contains(book)) {
                double similarity = borrowedBooks.stream()
                        .mapToDouble(b -> jaccardSimilarity(b, book))
                        .average()
                        .orElse(0.0);
                similarityScores.put(book, similarity);
            }
        }

        List<Long> recommendedBookIds = similarityScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topN)
                .map(Map.Entry::getKey)
                .toList();

        log.info("recommended books: {}", recommendedBookIds);

        List<BookForReaderResponseDto> books = bookDefinitionRepository.findByIdIn(recommendedBookIds).stream()
                .map(BookForReaderResponseDto::new)
                .toList();

        Map<Long, BookForReaderResponseDto> bookMap = books.stream()
                .collect(Collectors.toMap(BookForReaderResponseDto::getId, book -> book));

        return recommendedBookIds.stream()
                .map(bookMap::get)
                .toList();
    }

}
