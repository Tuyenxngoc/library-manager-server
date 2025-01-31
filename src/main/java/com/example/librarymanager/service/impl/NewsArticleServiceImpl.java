package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.EventConstants;
import com.example.librarymanager.constant.SortByDataConstant;
import com.example.librarymanager.constant.SuccessMessage;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.request.NewsArticleRequestDto;
import com.example.librarymanager.domain.dto.response.NewsArticleResponseDto;
import com.example.librarymanager.domain.entity.NewsArticle;
import com.example.librarymanager.domain.mapper.NewsArticleMapper;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.NewsArticleRepository;
import com.example.librarymanager.service.LogService;
import com.example.librarymanager.service.NewsArticleService;
import com.example.librarymanager.util.MaskingUtils;
import com.example.librarymanager.util.PaginationUtil;
import com.example.librarymanager.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsArticleServiceImpl implements NewsArticleService {

    private static final String TAG = "Quản lý bài viết";

    private final NewsArticleRepository newsArticleRepository;

    private final NewsArticleMapper newsArticleMapper;

    private final MessageSource messageSource;

    private final LogService logService;

    private final UploadFileUtil uploadFileUtil;

    @Override
    public CommonResponseDto save(NewsArticleRequestDto requestDto, MultipartFile imageFile, String userId) {
        NewsArticle newsArticle = newsArticleMapper.toNewsArticle(requestDto);
        newsArticle.setActiveFlag(true);

        //Kiểm tra file tải lên có phải định dạng ảnh không
        uploadFileUtil.checkImageIsValid(imageFile);

        // Xử lý upload ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = uploadFileUtil.uploadFile(imageFile);
            newsArticle.setImageUrl(imagePath);
        }

        newsArticle.setCreatedDate(LocalDate.now());
        newsArticle.setTitleSlug(MaskingUtils.toSlug(newsArticle.getTitle() + newsArticleRepository.count()));
        newsArticleRepository.save(newsArticle);

        logService.createLog(TAG, EventConstants.ADD, "Tạo bài viết mới: " + newsArticle.getTitle(), userId);

        String message = messageSource.getMessage(SuccessMessage.CREATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, newsArticle);
    }

    @Override
    public CommonResponseDto update(Long id, NewsArticleRequestDto requestDto, MultipartFile imageFile, String userId) {
        NewsArticle newsArticle = findById(id);

        //Kiểm tra file tải lên có phải định dạng ảnh không
        uploadFileUtil.checkImageIsValid(imageFile);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = uploadFileUtil.uploadFile(imageFile);

            //Xóa ảnh cũ
            uploadFileUtil.destroyFileWithUrl(newsArticle.getImageUrl());

            newsArticle.setImageUrl(imagePath);
        }

        newsArticle.setTitle(requestDto.getTitle());
        newsArticle.setNewsType(requestDto.getNewsType());
        newsArticle.setDescription(requestDto.getDescription());
        newsArticle.setContent(requestDto.getContent());
        newsArticle.setTitleSlug(MaskingUtils.toSlug(newsArticle.getTitle() + newsArticleRepository.count()));

        newsArticleRepository.save(newsArticle);

        logService.createLog(TAG, EventConstants.EDIT, "Cập nhật bài viết id: " + newsArticle.getId() + ", tiêu đề mới: " + newsArticle.getTitle(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, newsArticle);
    }

    @Override
    public CommonResponseDto delete(Long id, String userId) {
        NewsArticle newsArticle = findById(id);

        newsArticleRepository.delete(newsArticle);

        logService.createLog(TAG, EventConstants.DELETE, "Xóa bài viết: " + newsArticle.getTitle(), userId);

        String message = messageSource.getMessage(SuccessMessage.DELETE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public PaginationResponseDto<NewsArticle> findAll(PaginationFullRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.NEWS_ARTICLE);

        Page<NewsArticle> page = newsArticleRepository.findAll(
                EntitySpecification.filterNewsArticles(requestDto.getKeyword(), requestDto.getSearchBy(), requestDto.getActiveFlag()),
                pageable);

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.NEWS_ARTICLE, page);

        PaginationResponseDto<NewsArticle> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(page.getContent());
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public NewsArticle findById(Long id) {
        return newsArticleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NewsArticle.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public CommonResponseDto toggleActiveStatus(Long id, String userId) {
        NewsArticle newsArticle = findById(id);

        newsArticle.setActiveFlag(!newsArticle.getActiveFlag());

        newsArticleRepository.save(newsArticle);

        logService.createLog(TAG, EventConstants.EDIT, "Thay đổi trạng thái bài viết: " + newsArticle.getTitle(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, newsArticle.getActiveFlag());
    }

    @Override
    public PaginationResponseDto<NewsArticleResponseDto> getNewsArticles(PaginationFullRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.NEWS_ARTICLE);

        Page<NewsArticle> page = newsArticleRepository.findAll(
                EntitySpecification.filterNewsArticles(requestDto.getKeyword(), requestDto.getSearchBy(), true),
                pageable);

        List<NewsArticleResponseDto> items = page.getContent().stream()
                .map(NewsArticleResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.NEWS_ARTICLE, page);

        PaginationResponseDto<NewsArticleResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public NewsArticle getNewsArticleByTitleSlug(String titleSlug) {
        return newsArticleRepository.findByTitleSlug(titleSlug)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NewsArticle.ERR_NOT_FOUND_ID));
    }
}
