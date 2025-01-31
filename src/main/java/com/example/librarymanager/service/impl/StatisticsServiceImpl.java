package com.example.librarymanager.service.impl;

import com.example.librarymanager.domain.dto.pagination.PaginationRequestDto;
import com.example.librarymanager.domain.dto.response.statistics.*;
import com.example.librarymanager.repository.*;
import com.example.librarymanager.service.StatisticsService;
import com.example.librarymanager.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final PublisherRepository publisherRepository;

    private final ReaderRepository readerRepository;

    private final CartDetailRepository cartDetailRepository;

    private final BorrowReceiptRepository borrowReceiptRepository;

    private final BookBorrowRepository bookBorrowRepository;

    private final CategoryRepository categoryRepository;

    @Override
    public LibraryStatisticsResponseDto getLibraryStatistics() {
        long publications = bookRepository.count();
        long authors = authorRepository.count();
        long publishers = publisherRepository.count();
        long readers = readerRepository.count();

        return new LibraryStatisticsResponseDto(publications, authors, publishers, readers);
    }

    @Override
    public BorrowStatisticsResponseDto getBorrowStatistics() {
        int borrowRequests = cartDetailRepository.countBorrowRequests();
        int currentlyBorrowed = borrowReceiptRepository.countCurrentlyBorrowed();
        int dueToday = borrowReceiptRepository.countDueToday();
        int overdue = borrowReceiptRepository.countOverdue();

        return new BorrowStatisticsResponseDto(borrowRequests, currentlyBorrowed, dueToday, overdue);
    }

    @Override
    public LoanStatusResponseDto getLoanStatus() {
        double borrowedBooks = borrowReceiptRepository.countCurrentlyBorrowed();
        double overdueBooks = borrowReceiptRepository.countOverdue();
        double total = borrowedBooks + overdueBooks;

        double percentageBorrowed = total > 0 ? Math.round((borrowedBooks / total * 100) * 10) / 10.0 : 0;
        double percentageOverdue = total > 0 ? Math.round((overdueBooks / total * 100) * 10) / 10.0 : 0;

        return new LoanStatusResponseDto(borrowedBooks, overdueBooks, percentageBorrowed, percentageOverdue);
    }

    @Override
    public List<PublicationResponseDto> getMostBorrowedPublications() {
        return bookBorrowRepository.findTop5ByOrderByBorrowCountDesc();
    }

    @Override
    public List<CategoryStatisticsResponseDto> getPublicationCountByCategory(PaginationRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto);
        return categoryRepository.findCategoryStatistics(pageable);
    }
}
