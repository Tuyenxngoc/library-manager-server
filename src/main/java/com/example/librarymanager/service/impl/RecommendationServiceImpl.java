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
     * Calculates the Jaccard similarity coefficient between two books based on the readers who have borrowed them.
     * <p>
     * The Jaccard similarity is defined as the size of the intersection divided by the size of the union of two sets.
     * In this case, the sets are the readers who have borrowed each book.
     *
     * @param bookA The ID of the first book to compare.
     * @param bookB The ID of the second book to compare.
     * @return A double value between 0 and 1, where 0 indicates no similarity (no common readers)
     * and 1 indicates identical reader sets. If neither book has been borrowed by any readers,
     * the method returns 0.
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

    /**
     * Recommends books for a reader based on their borrowing history and similarity to other books.
     * <p>
     * This method uses the Jaccard similarity coefficient to calculate the similarity between books
     * based on the readers who have borrowed them. It then recommends books that are most similar
     * to the ones the reader has already borrowed, excluding books they have already read.
     *
     * @param cardNumber The unique identifier of the reader's library card. If null or empty, an empty list is returned.
     * @param topN       The maximum number of book recommendations to return.
     * @return A list of BookForReaderResponseDto objects representing the recommended books,
     * sorted by their similarity score in descending order. The list will contain at most
     * topN elements. If no recommendations are found or if the cardNumber is invalid,
     * an empty list is returned.
     */
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

        log.debug("similarity scores: {}", similarityScores);

        List<Long> recommendedBookIds = similarityScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topN)
                .map(Map.Entry::getKey)
                .toList();

        log.debug("recommended books: {}", recommendedBookIds);

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
