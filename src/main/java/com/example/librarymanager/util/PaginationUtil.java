package com.example.librarymanager.util;

import com.example.librarymanager.constant.CommonConstant;
import com.example.librarymanager.constant.SortByDataConstant;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationSortRequestDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PaginationUtil {

    public static Pageable buildPageable(PaginationRequestDto request) {
        return PageRequest.of(request.getPageNum(), request.getPageSize());
    }

    public static Pageable buildPageable(PaginationSortRequestDto requestDto, SortByDataConstant constant) {
        Sort sort = Sort.by(requestDto.getSortBy(constant));
        sort = requestDto.getIsAscending() ? sort.ascending() : sort.descending();
        return PageRequest.of(requestDto.getPageNum(), requestDto.getPageSize(), sort);
    }

    public static Pageable buildPageable(PaginationRequestDto requestDto, List<String> sortByFields, List<Boolean> sortDirections) {
        if (sortByFields.size() != sortDirections.size()) {
            throw new IllegalArgumentException("Number of sort fields and sort directions must match");
        }

        Sort sort = Sort.by(sortByFields.stream()
                .map(field -> sortDirections.get(sortByFields.indexOf(field)) ? Sort.Order.asc(field) : Sort.Order.desc(field))
                .toArray(Sort.Order[]::new)
        );

        return PageRequest.of(requestDto.getPageNum(), requestDto.getPageSize(), sort);
    }

    public static <T> PagingMeta buildPagingMeta(PaginationRequestDto request, Page<T> pages) {
        return buildPagingMeta(request.getPageNum(), request.getPageSize(), null, null, pages);
    }

    public static <T> PagingMeta buildPagingMeta(PaginationSortRequestDto request, SortByDataConstant constant, Page<T> pages) {
        String sortBy = request.getSortBy(constant);
        String sortOrder = request.getIsAscending() ? CommonConstant.SORT_TYPE_ASC : CommonConstant.SORT_TYPE_DESC;
        return buildPagingMeta(request.getPageNum(), request.getPageSize(), sortBy, sortOrder, pages);
    }

    public static <T> PagingMeta buildPagingMeta(PaginationFullRequestDto request, SortByDataConstant constant, Page<T> pages) {
        String sortBy = request.getSortBy(constant);
        String sortOrder = request.getIsAscending() ? CommonConstant.SORT_TYPE_ASC : CommonConstant.SORT_TYPE_DESC;
        return buildPagingMeta(request.getPageNum(), request.getPageSize(), sortBy, sortOrder, pages, request.getKeyword(), request.getSearchBy());
    }

    private static <T> PagingMeta buildPagingMeta(int pageNum, int pageSize, String sortBy, String sortOrder, Page<T> pages, String keyword, String searchBy) {
        return new PagingMeta(
                pages.getTotalElements(),
                pages.getTotalPages(),
                pageNum + CommonConstant.ONE_INT_VALUE,
                pageSize,
                sortBy,
                sortOrder,
                keyword,
                searchBy
        );
    }

    private static <T> PagingMeta buildPagingMeta(int pageNum, int pageSize, String sortBy, String sortOrder, Page<T> pages) {
        return buildPagingMeta(pageNum, pageSize, sortBy, sortOrder, pages, null, null);
    }
}