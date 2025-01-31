package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.SortByDataConstant;
import com.example.librarymanager.constant.SuccessMessage;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.request.ClassificationSymbolRequestDto;
import com.example.librarymanager.domain.entity.ClassificationSymbol;
import com.example.librarymanager.domain.mapper.ClassificationSymbolMapper;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.exception.BadRequestException;
import com.example.librarymanager.exception.ConflictException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.ClassificationSymbolRepository;
import com.example.librarymanager.service.ClassificationSymbolService;
import com.example.librarymanager.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassificationSymbolServiceImpl implements ClassificationSymbolService {

    private final ClassificationSymbolRepository classificationSymbolRepository;

    private final ClassificationSymbolMapper classificationSymbolMapper;

    private final MessageSource messageSource;

    @Override
    public void initClassificationSymbolsFromCsv(String classificationSymbolsCsvPath) {
        if (classificationSymbolRepository.count() > 0) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(classificationSymbolsCsvPath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                if (values.length < 3) continue;

                ClassificationSymbol classificationSymbol = new ClassificationSymbol();
                classificationSymbol.setName(values[0]);
                classificationSymbol.setCode(values[1]);
                try {
                    int level = Integer.parseInt(values[2].trim());
                    classificationSymbol.setLevel(level);
                } catch (NumberFormatException e) {
                    log.warn("Invalid level value for line '{}', must be an integer. Error: {}", line, e.getMessage());
                    continue;
                }

                if (!classificationSymbolRepository.existsByCode(classificationSymbol.getCode())) {
                    classificationSymbolRepository.save(classificationSymbol);
                }
            }
        } catch (IOException e) {
            log.error("Error while initializing classification symbol from CSV: {}", e.getMessage(), e);
        }
    }

    @Override
    public CommonResponseDto save(ClassificationSymbolRequestDto requestDto) {
        if (classificationSymbolRepository.existsByCode(requestDto.getCode())) {
            throw new ConflictException(ErrorMessage.ClassificationSymbol.ERR_DUPLICATE_CODE);
        }

        ClassificationSymbol classificationSymbol = classificationSymbolMapper.toClassificationSymbol(requestDto);

        classificationSymbol.setActiveFlag(true);
        classificationSymbolRepository.save(classificationSymbol);

        String message = messageSource.getMessage(SuccessMessage.CREATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, classificationSymbol);
    }

    @Override
    public CommonResponseDto update(Long id, ClassificationSymbolRequestDto requestDto) {
        ClassificationSymbol classificationSymbol = findById(id);

        if (!Objects.equals(classificationSymbol.getCode(), requestDto.getCode()) && classificationSymbolRepository.existsByCode(requestDto.getCode())) {
            throw new ConflictException(ErrorMessage.ClassificationSymbol.ERR_DUPLICATE_CODE);
        }

        classificationSymbol.setName(requestDto.getName());
        classificationSymbol.setCode(requestDto.getCode());
        classificationSymbol.setLevel(requestDto.getLevel());

        classificationSymbolRepository.save(classificationSymbol);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, classificationSymbol);
    }

    @Override
    public CommonResponseDto delete(Long id) {
        ClassificationSymbol classificationSymbol = findById(id);

        if (!classificationSymbol.getBookDefinitions().isEmpty()) {
            throw new BadRequestException(ErrorMessage.ClassificationSymbol.ERR_HAS_LINKED_BOOKS);
        }

        classificationSymbolRepository.delete(classificationSymbol);

        String message = messageSource.getMessage(SuccessMessage.DELETE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public PaginationResponseDto<ClassificationSymbol> findAll(PaginationFullRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.CLASSIFICATION_SYMBOL);

        Page<ClassificationSymbol> page = classificationSymbolRepository.findAll(
                EntitySpecification.filterClassificationSymbols(requestDto.getKeyword(), requestDto.getSearchBy(), requestDto.getActiveFlag()),
                pageable);

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.CLASSIFICATION_SYMBOL, page);

        PaginationResponseDto<ClassificationSymbol> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(page.getContent());
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public ClassificationSymbol findById(Long id) {
        return classificationSymbolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ClassificationSymbol.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public CommonResponseDto toggleActiveStatus(Long id) {
        ClassificationSymbol classificationSymbol = findById(id);

        classificationSymbol.setActiveFlag(!classificationSymbol.getActiveFlag());

        classificationSymbolRepository.save(classificationSymbol);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, classificationSymbol.getActiveFlag());
    }
}
