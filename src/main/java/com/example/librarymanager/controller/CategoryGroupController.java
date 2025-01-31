package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.CurrentUser;
import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.request.CategoryGroupRequestDto;
import com.example.librarymanager.security.CustomUserDetails;
import com.example.librarymanager.service.CategoryGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Category Group")
public class CategoryGroupController {

    CategoryGroupService categoryGroupService;

    @Operation(summary = "API Create Category Group")
    @PreAuthorize("hasRole('ROLE_MANAGE_CATEGORY_GROUP')")
    @PostMapping(UrlConstant.CategoryGroup.CREATE)
    public ResponseEntity<?> createCategoryGroup(
            @Valid @RequestBody CategoryGroupRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, categoryGroupService.save(requestDto, userDetails.getUserId()));
    }

    @Operation(summary = "API Update Category Group")
    @PreAuthorize("hasRole('ROLE_MANAGE_CATEGORY_GROUP')")
    @PutMapping(UrlConstant.CategoryGroup.UPDATE)
    public ResponseEntity<?> updateCategoryGroup(
            @PathVariable Long id,
            @Valid @RequestBody CategoryGroupRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(categoryGroupService.update(id, requestDto, userDetails.getUserId()));
    }

    @Operation(summary = "API Delete Category Group")
    @PreAuthorize("hasRole('ROLE_MANAGE_CATEGORY_GROUP')")
    @DeleteMapping(UrlConstant.CategoryGroup.DELETE)
    public ResponseEntity<?> deleteCategoryGroup(
            @PathVariable Long id,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(categoryGroupService.delete(id, userDetails.getUserId()));
    }

    @Operation(summary = "API Get Category Groups")
    @PreAuthorize("hasAnyRole('ROLE_MANAGE_CATEGORY_GROUP', 'ROLE_MANAGE_CATEGORY')")
    @GetMapping(UrlConstant.CategoryGroup.GET_ALL)
    public ResponseEntity<?> getCategoryGroups(@ParameterObject PaginationFullRequestDto requestDto) {
        return VsResponseUtil.success(categoryGroupService.findAll(requestDto));
    }

    @Operation(summary = "API Get Category Groups Tree")
    @PreAuthorize("hasAnyRole('ROLE_MANAGE_CATEGORY_GROUP', 'ROLE_MANAGE_BOOK')")
    @GetMapping(UrlConstant.CategoryGroup.GET_TREE)
    public ResponseEntity<?> getCategoryGroupsTree() {
        return VsResponseUtil.success(categoryGroupService.findTree());
    }

    @Operation(summary = "API Get Category Group By Id")
    @PreAuthorize("hasRole('ROLE_MANAGE_CATEGORY_GROUP')")
    @GetMapping(UrlConstant.CategoryGroup.GET_BY_ID)
    public ResponseEntity<?> getCategoryGroupById(@PathVariable Long id) {
        return VsResponseUtil.success(categoryGroupService.findById(id));
    }

    @Operation(summary = "API Toggle Active Status of Category Group")
    @PreAuthorize("hasRole('ROLE_MANAGE_CATEGORY_GROUP')")
    @PatchMapping(UrlConstant.CategoryGroup.TOGGLE_ACTIVE)
    public ResponseEntity<?> toggleActiveStatus(
            @PathVariable Long id,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(categoryGroupService.toggleActiveStatus(id, userDetails.getUserId()));
    }

}
