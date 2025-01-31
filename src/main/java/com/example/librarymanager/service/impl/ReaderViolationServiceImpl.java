package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.CardStatus;
import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.EventConstants;
import com.example.librarymanager.constant.SortByDataConstant;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.request.ReaderViolationRequestDto;
import com.example.librarymanager.domain.dto.response.reader.ReaderViolationResponseDto;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.domain.entity.ReaderViolation;
import com.example.librarymanager.domain.mapper.ReaderViolationMapper;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.ReaderRepository;
import com.example.librarymanager.repository.ReaderViolationRepository;
import com.example.librarymanager.service.LogService;
import com.example.librarymanager.service.ReaderViolationService;
import com.example.librarymanager.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReaderViolationServiceImpl implements ReaderViolationService {
    private static final String TAG = "Quản lý vi phạm của bạn đọc";

    private final ReaderViolationRepository readerViolationRepository;

    private final ReaderViolationMapper readerViolationMapper;

    private final LogService logService;

    private final ReaderRepository readerRepository;

    private ReaderViolation getEntity(Long id) {
        return readerViolationRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.ReaderViolation.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public CommonResponseDto save(ReaderViolationRequestDto requestDto, String userId) {
        ReaderViolation violation = readerViolationMapper.toReaderViolation(requestDto);
        getReader(requestDto, violation);

        readerViolationRepository.save(violation);

        logService.createLog(TAG, EventConstants.ADD, "Tạo vi phạm mới cho bạn đọc: " + violation.getViolationDetails(), userId);

        return new CommonResponseDto("Vi phạm đã được thêm thành công.", new ReaderViolationResponseDto(violation));
    }

    private void getReader(ReaderViolationRequestDto requestDto, ReaderViolation violation) {
        Reader reader = readerRepository.findById(requestDto.getReaderId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Reader.ERR_NOT_FOUND_ID, requestDto.getReaderId()));
        violation.setReader(reader);

        switch (violation.getPenaltyForm()) {
            case CARD_REVOCATION -> reader.setStatus(CardStatus.REVOKED);
            case CARD_SUSPENSION -> reader.setStatus(CardStatus.SUSPENDED);
        }
    }

    @Override
    public CommonResponseDto update(Long id, ReaderViolationRequestDto requestDto, String userId) {
        ReaderViolation violation = getEntity(id);

        if (!Objects.equals(violation.getReader().getId(), requestDto.getReaderId())) {
            getReader(requestDto, violation);
        }

        violation.setViolationDetails(requestDto.getViolationDetails());
        violation.setPenaltyForm(requestDto.getPenaltyForm());
        violation.setOtherPenaltyForm(requestDto.getOtherPenaltyForm());
        violation.setPenaltyDate(requestDto.getPenaltyDate());
        violation.setEndDate(requestDto.getEndDate());
        violation.setFineAmount(requestDto.getFineAmount());
        violation.setNotes(requestDto.getNotes());

        readerViolationRepository.save(violation);

        logService.createLog(TAG, EventConstants.EDIT, "Cập nhật vi phạm id: " + violation.getId(), userId);

        return new CommonResponseDto("Vi phạm đã được cập nhật thành công.", new ReaderViolationResponseDto(violation));
    }

    @Override
    public CommonResponseDto delete(Long id, String userId) {
        ReaderViolation violation = getEntity(id);

        readerViolationRepository.delete(violation);

        logService.createLog(TAG, EventConstants.DELETE, "Xóa vi phạm id: " + violation.getId(), userId);

        return new CommonResponseDto("Vi phạm đã được xóa thành công.");
    }

    @Override
    public PaginationResponseDto<ReaderViolationResponseDto> findAll(PaginationFullRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.READER_VIOLATION);

        Page<ReaderViolation> page = readerViolationRepository.findAll(
                EntitySpecification.filterReaderViolations(requestDto.getKeyword(), requestDto.getSearchBy()),
                pageable);

        List<ReaderViolationResponseDto> items = page.getContent().stream()
                .map(ReaderViolationResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.READER_VIOLATION, page);

        PaginationResponseDto<ReaderViolationResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public ReaderViolationResponseDto findById(Long id) {
        ReaderViolation readerViolation = getEntity(id);
        return new ReaderViolationResponseDto(readerViolation);
    }
}
