package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.EventConstants;
import com.example.librarymanager.constant.SortByDataConstant;
import com.example.librarymanager.constant.SuccessMessage;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.request.CategoryGroupRequestDto;
import com.example.librarymanager.domain.dto.response.category.CategoryGroupTree;
import com.example.librarymanager.domain.entity.CategoryGroup;
import com.example.librarymanager.domain.mapper.CategoryGroupMapper;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.exception.BadRequestException;
import com.example.librarymanager.exception.ConflictException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.CategoryGroupRepository;
import com.example.librarymanager.service.CategoryGroupService;
import com.example.librarymanager.service.LogService;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryGroupServiceImpl implements CategoryGroupService {

    private static final String TAG = "Quản lý nhóm danh mục";

    private final LogService logService;

    private final CategoryGroupRepository categoryGroupRepository;

    private final CategoryGroupMapper categoryGroupMapper;

    private final MessageSource messageSource;

    @Override
    public void initCategoryGroupsFromCsv(String categoryGroupsCsvPath) {
        if (categoryGroupRepository.count() > 0) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(categoryGroupsCsvPath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                if (values.length < 1) continue;

                CategoryGroup categoryGroup = new CategoryGroup();
                categoryGroup.setGroupName(values[0]);

                if (!categoryGroupRepository.existsByGroupName(categoryGroup.getGroupName())) {
                    categoryGroupRepository.save(categoryGroup);
                }
            }
        } catch (IOException e) {
            log.error("Error while initializing category groups from CSV: {}", e.getMessage(), e);
        }
    }

    @Override
    public CommonResponseDto save(CategoryGroupRequestDto requestDto, String userId) {
        if (categoryGroupRepository.existsByGroupName(requestDto.getGroupName())) {
            throw new ConflictException(ErrorMessage.CategoryGroup.ERR_DUPLICATE_GROUP_NAME);
        }

        CategoryGroup categoryGroup = categoryGroupMapper.toCategoryGroup(requestDto);

        categoryGroup.setActiveFlag(true);
        categoryGroupRepository.save(categoryGroup);

        logService.createLog(TAG, EventConstants.ADD, "Thêm nhóm danh mục mới Id: " + categoryGroup.getId(), userId);

        String message = messageSource.getMessage(SuccessMessage.CREATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, categoryGroup);
    }

    @Override
    public CommonResponseDto update(Long id, CategoryGroupRequestDto requestDto, String userId) {
        if (categoryGroupRepository.existsByGroupName(requestDto.getGroupName())) {
            throw new ConflictException(ErrorMessage.CategoryGroup.ERR_DUPLICATE_GROUP_NAME);
        }

        CategoryGroup categoryGroup = findById(id);

        categoryGroup.setGroupName(requestDto.getGroupName());

        categoryGroupRepository.save(categoryGroup);

        logService.createLog(TAG, EventConstants.EDIT, "Cập nhật nhóm danh mục Id: " + categoryGroup.getId(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, categoryGroup);
    }

    @Override
    public CommonResponseDto delete(Long id, String userId) {
        CategoryGroup categoryGroup = findById(id);

        if (!categoryGroup.getCategories().isEmpty()) {
            throw new BadRequestException(ErrorMessage.CategoryGroup.ERR_HAS_LINKED_CATEGORIES);
        }

        categoryGroupRepository.delete(categoryGroup);

        logService.createLog(TAG, EventConstants.DELETE, "Xóa nhóm danh mục Id: " + categoryGroup.getId(), userId);

        String message = messageSource.getMessage(SuccessMessage.DELETE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, true);
    }

    @Override
    public PaginationResponseDto<CategoryGroup> findAll(PaginationFullRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.CATEGORY_GROUP);

        Page<CategoryGroup> page = categoryGroupRepository.findAll(
                EntitySpecification.filterCategoryGroups(requestDto.getKeyword(), requestDto.getSearchBy(), requestDto.getActiveFlag()),
                pageable);

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.CATEGORY_GROUP, page);

        PaginationResponseDto<CategoryGroup> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(page.getContent());
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public CategoryGroup findById(Long id) {
        return categoryGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.CategoryGroup.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public CommonResponseDto toggleActiveStatus(Long id, String userId) {
        CategoryGroup categoryGroup = findById(id);

        categoryGroup.setActiveFlag(!categoryGroup.getActiveFlag());

        categoryGroupRepository.save(categoryGroup);

        logService.createLog(TAG, EventConstants.EDIT, "Thay đổi trạng thái nhóm danh mục Id: " + categoryGroup.getId() + ", trạng thái: " + categoryGroup.getActiveFlag(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, categoryGroup.getActiveFlag());
    }

    @Override
    public List<CategoryGroupTree> findTree() {
        List<CategoryGroupTree> groupTrees = categoryGroupRepository.findAll().stream()
                .map(CategoryGroupTree::new)
                .toList();

        int totalCount = groupTrees.stream()
                .mapToInt(CategoryGroupTree::getCount)
                .sum();

        CategoryGroupTree allCategoriesGroup = new CategoryGroupTree(-1, "Tất cả", totalCount, new ArrayList<>());

        List<CategoryGroupTree> responseDto = new ArrayList<>();
        responseDto.add(allCategoriesGroup);
        responseDto.addAll(groupTrees);

        return responseDto;
    }

}