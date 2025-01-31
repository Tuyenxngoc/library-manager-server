package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.SortByDataConstant;
import com.example.librarymanager.domain.dto.filter.LogFilter;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.response.LogResponseDto;
import com.example.librarymanager.domain.entity.Log;
import com.example.librarymanager.domain.entity.User;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.repository.LogRepository;
import com.example.librarymanager.service.LogService;
import com.example.librarymanager.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    @Override
    public PaginationResponseDto<LogResponseDto> findAll(PaginationFullRequestDto requestDto, LogFilter logFilter) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.LOG);

        Page<Log> page = logRepository.findAll(
                EntitySpecification.filterLogs(requestDto.getKeyword(), requestDto.getSearchBy(), logFilter),
                pageable);

        List<LogResponseDto> items = page.getContent().stream()
                .map(LogResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.LOG, page);

        PaginationResponseDto<LogResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public void createLog(String feature, String event, String content, String userId) {
        User user = new User(userId);
        Log l = new Log();
        l.setFeature(feature);
        l.setEvent(event);
        l.setContent(content);
        l.setTimestamp(LocalDateTime.now());
        l.setUser(user);

        try {
            logRepository.save(l);
        } catch (Exception e) {
            log.error("Error occurred while creating log: {}", e.getMessage());
        }
    }
}