package com.example.librarymanager.service.impl;

import com.example.librarymanager.domain.dto.response.bookdefinition.BookForReaderResponseDto;
import com.example.librarymanager.repository.BookBorrowRepository;
import com.example.librarymanager.repository.BookDefinitionRepository;
import com.example.librarymanager.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final BookDefinitionRepository bookDefinitionRepository;

    private final BookBorrowRepository bookBorrowRepository;

    private List<BookForReaderResponseDto> mapIdsToBooks(List<Long> bookIds) {
        List<BookForReaderResponseDto> books = bookDefinitionRepository.findByIdIn(bookIds);

        Map<Long, BookForReaderResponseDto> bookMap = books.stream()
                .collect(Collectors.toMap(BookForReaderResponseDto::getId, book -> book));

        return bookIds.stream()
                .map(bookMap::get)
                .toList();
    }

    /**
     * Calculates the Jaccard similarity coefficient between two sets of users.
     * The Jaccard similarity is defined as the size of the intersection divided by the size of the union of the two sets.
     *
     * @param usersA The first set of user IDs
     * @param usersB The second set of user IDs
     * @return A double value between 0 and 1, where 0 indicates no similarity and 1 indicates identical sets.
     * Returns 0 if both sets are empty.
     */
    private double jaccardSimilarity(Set<Long> usersA, Set<Long> usersB) {
        if (usersA.isEmpty() || usersB.isEmpty()) return 0.0;

        Set<Long> smaller = (usersA.size() < usersB.size()) ? usersA : usersB;
        Set<Long> larger = (usersA.size() < usersB.size()) ? usersB : usersA;

        int intersectionSize = 0;
        for (Long user : smaller) {
            if (larger.contains(user)) {
                intersectionSize++;
            }
        }

        int unionSize = usersA.size() + usersB.size() - intersectionSize;

        return (double) intersectionSize / unionSize;
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
        // Lấy danh sách sách đã mượn
        Set<Long> borrowedBooks = bookBorrowRepository.getBorrowedBookDefinitionIds(cardNumber);
        Set<Long> allBooks = bookDefinitionRepository.findAllBookDefinitionIdsWithBook();

        // Tiền xử lý: Lấy danh sách người dùng đã mượn từng cuốn sách
        Map<Long, Set<Long>> bookReadersMap = allBooks.stream()
                .collect(Collectors.toMap(book -> book, bookBorrowRepository::getReadersBorrowed));

        Map<Long, Double> similarityScores = new HashMap<>();

        for (Long book : allBooks) {
            if (!borrowedBooks.contains(book)) {
                double similarity = borrowedBooks.stream()
                        .mapToDouble(b -> jaccardSimilarity(bookReadersMap.get(b), bookReadersMap.get(book)))
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

        log.debug("similarity scores: {}", similarityScores);
        log.debug("recommended books: {}", recommendedBookIds);

        return mapIdsToBooks(recommendedBookIds);
    }

}
