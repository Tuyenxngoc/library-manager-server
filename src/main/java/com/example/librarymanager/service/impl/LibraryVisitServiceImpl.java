package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.SortByDataConstant;
import com.example.librarymanager.constant.SuccessMessage;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.filter.LibraryVisitFilter;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.request.LibraryVisitRequestDto;
import com.example.librarymanager.domain.dto.response.LibraryVisitResponseDto;
import com.example.librarymanager.domain.entity.LibraryVisit;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.exception.ForbiddenException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.LibraryVisitRepository;
import com.example.librarymanager.repository.ReaderRepository;
import com.example.librarymanager.service.LibraryVisitService;
import com.example.librarymanager.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryVisitServiceImpl implements LibraryVisitService {

    private final LibraryVisitRepository libraryVisitRepository;

    private final ReaderRepository readerRepository;

    private final MessageSource messageSource;

    @Override
    public CommonResponseDto save(LibraryVisitRequestDto requestDto) {
        // Lấy ra bạn đọc
        Reader reader = readerRepository.findByCardNumber(requestDto.getCardNumber())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Reader.ERR_NOT_FOUND_CARD_NUMBER, requestDto.getCardNumber()));
        switch (reader.getStatus()) {//todo
            case INACTIVE -> throw new ForbiddenException(ErrorMessage.Reader.ERR_READER_INACTIVE);
            case SUSPENDED -> throw new ForbiddenException(ErrorMessage.Reader.ERR_READER_SUSPENDED);
            case REVOKED -> throw new ForbiddenException(ErrorMessage.Reader.ERR_READER_REVOKED);
        }
        if (reader.getExpiryDate().isBefore(LocalDate.now())) {
            throw new ForbiddenException(ErrorMessage.Reader.ERR_READER_EXPIRED);
        }

        // Lấy ra thời gian bắt đầu và kết thúc của ngày hôm nay
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // Tìm lần truy cập gần nhất trong ngày
        LibraryVisit lastVisit = libraryVisitRepository.findTopByReaderIdAndEntryTimeBetweenOrderByEntryTimeDesc(reader.getId(), startOfDay, endOfDay);

        if (lastVisit != null && lastVisit.getExitTime() == null) {
            // Nếu đã có lần truy cập trong ngày và chưa có thời gian thoát, cập nhật thời gian thoát
            lastVisit.setExitTime(LocalDateTime.now());
            libraryVisitRepository.save(lastVisit);

            String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
            return new CommonResponseDto(message, new LibraryVisitResponseDto(lastVisit));
        } else {
            // Nếu đã có lượt truy cập và thời gian thoát, tạo mới
            LibraryVisit newVisit = new LibraryVisit();
            newVisit.setReader(reader);
            newVisit.setEntryTime(LocalDateTime.now());
            libraryVisitRepository.save(newVisit);

            String message = messageSource.getMessage(SuccessMessage.CREATE, null, LocaleContextHolder.getLocale());
            return new CommonResponseDto(message, new LibraryVisitResponseDto(newVisit));
        }
    }

    @Override
    public CommonResponseDto update(Long id, LibraryVisitRequestDto requestDto) {
        return null;
    }

    @Override
    public PaginationResponseDto<LibraryVisitResponseDto> findAll(PaginationFullRequestDto requestDto, LibraryVisitFilter filter) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.LIBRARY_VISIT);

        Page<LibraryVisit> page = libraryVisitRepository.findAll(
                EntitySpecification.filterLibraryVisits(requestDto.getKeyword(), requestDto.getSearchBy(), filter),
                pageable);

        List<LibraryVisitResponseDto> items = page.getContent().stream()
                .map(LibraryVisitResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.LIBRARY_VISIT, page);

        PaginationResponseDto<LibraryVisitResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public LibraryVisitResponseDto findById(Long id) {
        return null;
    }

    @Override
    public CommonResponseDto closeLibrary() {
        // Lấy thời gian đầu ngày và cuối ngày hôm nay
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // Lấy tất cả các lần truy cập trong ngày hôm nay mà chưa có exitTime
        List<LibraryVisit> visitsToday = libraryVisitRepository.findAllByEntryTimeBetweenAndExitTimeIsNull(startOfDay, endOfDay);

        // Cập nhật exitTime cho tất cả các lần truy cập đó
        visitsToday.forEach(visit -> visit.setExitTime(LocalDateTime.now()));

        // Lưu lại các thay đổi
        libraryVisitRepository.saveAll(visitsToday);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }
}