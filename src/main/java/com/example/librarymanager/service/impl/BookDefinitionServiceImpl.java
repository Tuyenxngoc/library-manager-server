package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.EventConstants;
import com.example.librarymanager.constant.SortByDataConstant;
import com.example.librarymanager.constant.SuccessMessage;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.filter.BookDefinitionFilter;
import com.example.librarymanager.domain.dto.filter.QueryFilter;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationSortRequestDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.request.BookDefinitionRequestDto;
import com.example.librarymanager.domain.dto.response.bookdefinition.BookByBookDefinitionResponseDto;
import com.example.librarymanager.domain.dto.response.bookdefinition.BookDefinitionResponseDto;
import com.example.librarymanager.domain.dto.response.bookdefinition.BookDetailForReaderResponseDto;
import com.example.librarymanager.domain.dto.response.bookdefinition.BookForReaderResponseDto;
import com.example.librarymanager.domain.entity.*;
import com.example.librarymanager.domain.mapper.BookDefinitionMapper;
import com.example.librarymanager.domain.specification.BookSpecification;
import com.example.librarymanager.exception.BadRequestException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.*;
import com.example.librarymanager.service.BookDefinitionService;
import com.example.librarymanager.service.LogService;
import com.example.librarymanager.service.PdfService;
import com.example.librarymanager.util.PaginationUtil;
import com.example.librarymanager.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.librarymanager.domain.specification.EntitySpecification.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookDefinitionServiceImpl implements BookDefinitionService {

    private static final String TAG = "Quản lý biên mục";

    private final UploadFileUtil uploadFileUtil;

    private final BookDefinitionRepository bookDefinitionRepository;

    private final BookDefinitionMapper bookDefinitionMapper;

    private final MessageSource messageSource;

    private final CategoryRepository categoryRepository;

    private final BookSetRepository bookSetRepository;

    private final PublisherRepository publisherRepository;

    private final AuthorRepository authorRepository;

    private final BookAuthorRepository bookAuthorRepository;

    private final ClassificationSymbolRepository classificationSymbolRepository;

    private final BookRepository bookRepository;

    private final LogService logService;

    private final PdfService pdfService;

    @Override
    public void initBookDefinitionsFromCsv(String bookDefinitionsCsvPath) {
        if (bookDefinitionRepository.count() > 0) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(bookDefinitionsCsvPath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                if (values.length < 3) continue;

                BookDefinition bookDefinition = new BookDefinition();
                bookDefinition.setTitle(values[0]);
                bookDefinition.setBookCode(values[1]);

                Long categoryId = Long.parseLong(values[2]);
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Category not found for id: " + categoryId));
                bookDefinition.setCategory(category);

                if (!bookDefinitionRepository.existsByBookCode(bookDefinition.getBookCode())) {
                    bookDefinitionRepository.save(bookDefinition);
                }
            }
        } catch (IOException e) {
            log.error("Error while initializing book definitions from CSV: {}", e.getMessage(), e);
        }
    }

    @Override
    public CommonResponseDto save(BookDefinitionRequestDto requestDto, MultipartFile file, String userId) {
        //Kiểm tra file tải lên có phải định dạng ảnh không
        uploadFileUtil.checkImageIsValid(file);

        //Kiểm tra kí hiệu tên sách
        if (bookDefinitionRepository.existsByBookCode(requestDto.getBookCode())) {
            throw new BadRequestException(ErrorMessage.BookDefinition.ERR_DUPLICATE_CODE, requestDto.getBookCode());
        }

        //Map dữ liệu
        BookDefinition bookDefinition = bookDefinitionMapper.toBookDefinition(requestDto);

        //Lưu danh mục
        Category category = categoryRepository.findByIdAndActiveFlagIsTrue(requestDto.getCategoryId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Category.ERR_NOT_FOUND_ID, requestDto.getCategoryId()));
        bookDefinition.setCategory(category);

        //Lưu vào bộ sách
        if (requestDto.getBookSetId() != null) {
            BookSet bookSet = bookSetRepository.findByIdAndActiveFlagIsTrue(requestDto.getBookSetId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.BookSet.ERR_NOT_FOUND_ID, requestDto.getBookSetId()));
            bookDefinition.setBookSet(bookSet);
        }

        //Lưu nhà xuất bản
        if (requestDto.getPublisherId() != null) {
            Publisher publisher = publisherRepository.findByIdAndActiveFlagIsTrue(requestDto.getPublisherId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.Publisher.ERR_NOT_FOUND_ID, requestDto.getPublisherId()));
            bookDefinition.setPublisher(publisher);
        }

        //Lưu danh mục phân loại
        if (requestDto.getClassificationSymbolId() != null) {
            ClassificationSymbol classificationSymbol = classificationSymbolRepository.findByIdAndActiveFlagIsTrue(requestDto.getClassificationSymbolId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.ClassificationSymbol.ERR_NOT_FOUND_ID, requestDto.getClassificationSymbolId()));
            bookDefinition.setClassificationSymbol(classificationSymbol);
        }

        //Lưu danh sách tác giả
        if (requestDto.getAuthorIds() != null) {
            requestDto.getAuthorIds().forEach(authorId -> {
                Author author = authorRepository.findByIdAndActiveFlagIsTrue(authorId)
                        .orElseThrow(() -> new NotFoundException(ErrorMessage.Author.ERR_NOT_FOUND_ID, authorId));

                BookAuthor bookAuthor = new BookAuthor();
                bookAuthor.setAuthor(author);
                bookAuthor.setBookDefinition(bookDefinition);

                bookDefinition.getBookAuthors().add(bookAuthor);
            });
        }

        // Xử lý upload ảnh, ưu tiên file upload, rồi đến image url
        if (file != null && !file.isEmpty()) {
            String newImageUrl = uploadFileUtil.uploadFile(file);
            bookDefinition.setImageUrl(newImageUrl);
        } else if (requestDto.getImageUrl() != null) {
            String newImageUrl = uploadFileUtil.copyImageFromUrl(requestDto.getImageUrl());
            bookDefinition.setImageUrl(newImageUrl);
        }

        bookDefinition.setActiveFlag(true);
        bookDefinitionRepository.save(bookDefinition);

        logService.createLog(TAG, EventConstants.ADD, "Thêm biên mục mới: " + bookDefinition.getTitle(), userId);

        String message = messageSource.getMessage(SuccessMessage.CREATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public CommonResponseDto update(Long id, BookDefinitionRequestDto requestDto, MultipartFile file, String userId) {
        //Kiểm tra file tải lên có phải định dạng ảnh không
        uploadFileUtil.checkImageIsValid(file);

        // Tìm bookDefinition dựa trên id
        BookDefinition bookDefinition = bookDefinitionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.BookDefinition.ERR_NOT_FOUND_ID, id));

        //Kiểm tra kí hiệu tên sách
        if (!Objects.equals(bookDefinition.getBookCode(), requestDto.getBookCode()) && bookDefinitionRepository.existsByBookCode(requestDto.getBookCode())) {
            throw new BadRequestException(ErrorMessage.BookDefinition.ERR_DUPLICATE_CODE, requestDto.getBookCode());
        }

        // Cập nhật danh mục
        Category category = categoryRepository.findByIdAndActiveFlagIsTrue(requestDto.getCategoryId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Category.ERR_NOT_FOUND_ID, requestDto.getCategoryId()));
        bookDefinition.setCategory(category);

        // Cập nhật bộ sách
        if (requestDto.getBookSetId() != null) {
            BookSet bookSet = bookSetRepository.findByIdAndActiveFlagIsTrue(requestDto.getBookSetId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.BookSet.ERR_NOT_FOUND_ID, requestDto.getBookSetId()));
            bookDefinition.setBookSet(bookSet);
        } else {
            bookDefinition.setBookSet(null);
        }

        // Cập nhật nhà xuất bản
        if (requestDto.getPublisherId() != null) {
            Publisher publisher = publisherRepository.findByIdAndActiveFlagIsTrue(requestDto.getPublisherId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.Publisher.ERR_NOT_FOUND_ID, requestDto.getPublisherId()));
            bookDefinition.setPublisher(publisher);
        } else {
            bookDefinition.setPublisher(null);
        }

        // Cập nhật biểu tượng phân loại
        if (requestDto.getClassificationSymbolId() != null) {
            ClassificationSymbol classificationSymbol = classificationSymbolRepository.findByIdAndActiveFlagIsTrue(requestDto.getClassificationSymbolId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.ClassificationSymbol.ERR_NOT_FOUND_ID, requestDto.getClassificationSymbolId()));
            bookDefinition.setClassificationSymbol(classificationSymbol);
        } else {
            bookDefinition.setClassificationSymbol(null);
        }

        // Cập nhật danh sách tác giả
        Set<Long> newAuthorIds = requestDto.getAuthorIds(); // Mảng tác giả mới

        // Xóa các tác giả không còn trong danh sách mới
        if (newAuthorIds == null || newAuthorIds.isEmpty()) {
            bookAuthorRepository.deleteAllByBookDefinitionId(bookDefinition.getId()); // Xóa các bản ghi trong cơ sở dữ liệu
            bookDefinition.getBookAuthors().clear(); // Xóa các bản ghi trong bộ nhớ
        } else {
            // Tập hợp ID tác giả hiện tại
            Set<Long> currentAuthorIds = bookDefinition.getBookAuthors().stream()
                    .map(bookAuthor -> bookAuthor.getAuthor().getId())
                    .collect(Collectors.toSet());

            // Xóa các tác giả không có trong danh sách mới
            for (Long currentAuthorId : currentAuthorIds) {
                if (!newAuthorIds.contains(currentAuthorId)) {
                    // Tìm BookAuthor để xóa
                    BookAuthor bookAuthorToRemove = bookDefinition.getBookAuthors().stream()
                            .filter(bookAuthor -> bookAuthor.getAuthor().getId().equals(currentAuthorId))
                            .findFirst()
                            .orElse(null);
                    if (bookAuthorToRemove != null) {
                        bookDefinition.getBookAuthors().remove(bookAuthorToRemove);
                        bookAuthorRepository.delete(bookAuthorToRemove); // Xóa trong cơ sở dữ liệu
                    }
                }
            }

            // Thêm các tác giả mới
            for (Long authorId : newAuthorIds) {
                if (!currentAuthorIds.contains(authorId)) { // Nếu tác giả chưa tồn tại
                    Author author = authorRepository.findByIdAndActiveFlagIsTrue(authorId)
                            .orElseThrow(() -> new NotFoundException(ErrorMessage.Author.ERR_NOT_FOUND_ID, authorId));

                    BookAuthor bookAuthor = new BookAuthor();
                    bookAuthor.setAuthor(author);
                    bookAuthor.setBookDefinition(bookDefinition);
                    bookDefinition.getBookAuthors().add(bookAuthor);
                }
            }
        }

        // Cập nhật các thông tin khác
        bookDefinition.setTitle(requestDto.getTitle());
        bookDefinition.setPublishingYear(requestDto.getPublishingYear());
        bookDefinition.setPrice(requestDto.getPrice());
        bookDefinition.setEdition(requestDto.getEdition());
        bookDefinition.setReferencePrice(requestDto.getReferencePrice());
        bookDefinition.setPublicationPlace(requestDto.getPublicationPlace());
        bookDefinition.setBookCode(requestDto.getBookCode());
        bookDefinition.setPageCount(requestDto.getPageCount());
        bookDefinition.setBookSize(requestDto.getBookSize());
        bookDefinition.setParallelTitle(requestDto.getParallelTitle());
        bookDefinition.setSummary(requestDto.getSummary());
        bookDefinition.setSubtitle(requestDto.getSubtitle());
        bookDefinition.setAdditionalMaterial(requestDto.getAdditionalMaterial());
        bookDefinition.setKeywords(requestDto.getKeywords());
        bookDefinition.setIsbn(requestDto.getIsbn());
        bookDefinition.setLanguage(requestDto.getLanguage());
        bookDefinition.setSeries(requestDto.getSeries());
        bookDefinition.setAdditionalInfo(requestDto.getAdditionalInfo());

        // Xử lý upload ảnh, ưu tiên file upload, rồi đến image url
        if (file != null && !file.isEmpty()) {
            String newImageUrl = uploadFileUtil.uploadFile(file);

            //Xóa ảnh cũ
            uploadFileUtil.destroyFileWithUrl(bookDefinition.getImageUrl());

            bookDefinition.setImageUrl(newImageUrl);
        }

        // Lưu đối tượng bookDefinition đã cập nhật
        bookDefinitionRepository.save(bookDefinition);

        //Ghi log
        logService.createLog(TAG, EventConstants.EDIT, "Cập nhật biên mục id: " + bookDefinition.getId(), userId);

        // Trả về kết quả sau khi cập nhật thành công
        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public CommonResponseDto delete(Long id, String userId) {
        BookDefinition bookDefinition = findEntityById(id);

        if (!bookDefinition.getBooks().isEmpty()) {
            throw new BadRequestException(ErrorMessage.BookDefinition.ERR_HAS_LINKED_BOOKS);
        }

        uploadFileUtil.destroyFileWithUrl(bookDefinition.getImageUrl());

        bookDefinitionRepository.delete(bookDefinition);

        logService.createLog(TAG, EventConstants.DELETE, "Xóa biên mục: " + bookDefinition.getTitle(), userId);

        String message = messageSource.getMessage(SuccessMessage.DELETE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public PaginationResponseDto<BookDefinitionResponseDto> findAll(PaginationFullRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.BOOK_DEFINITION);

        Specification<BookDefinition> spec = Specification.where(baseFilterBookDefinitions(requestDto.getKeyword(), requestDto.getSearchBy(), requestDto.getActiveFlag()));

        Page<BookDefinition> page = bookDefinitionRepository.findAll(spec, pageable);

        List<BookDefinitionResponseDto> items = page.getContent().stream()
                .map(BookDefinitionResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.BOOK_DEFINITION, page);

        PaginationResponseDto<BookDefinitionResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public List<BookDefinitionResponseDto> findByIds(Set<Long> ids) {
        return bookDefinitionRepository.findBookDefinitionsByIds(ids);
    }

    @Override
    public BookDefinitionResponseDto findById(Long id) {
        BookDefinition bookDefinition = findEntityById(id);

        return new BookDefinitionResponseDto(bookDefinition);
    }

    private BookDefinition findEntityById(Long id) {
        return bookDefinitionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.BookDefinition.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public CommonResponseDto toggleActiveStatus(Long id, String userId) {
        BookDefinition bookDefinition = findEntityById(id);

        bookDefinition.setActiveFlag(!bookDefinition.getActiveFlag());

        bookDefinitionRepository.save(bookDefinition);

        logService.createLog(TAG, EventConstants.EDIT, "Thay đổi trạng thái biên mục: " + bookDefinition.getActiveFlag(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, bookDefinition.getActiveFlag());
    }

    @Override
    public PaginationResponseDto<BookByBookDefinitionResponseDto> getBooks(PaginationFullRequestDto requestDto, Long categoryGroupId, Long categoryId) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.BOOK_DEFINITION);

        Specification<BookDefinition> spec = Specification.where(baseFilterBookDefinitions(requestDto.getKeyword(), requestDto.getSearchBy(), requestDto.getActiveFlag()))
                .and(filterByCategoryId(categoryId))
                .and(filterByCategoryGroupId(categoryGroupId));

        Page<BookDefinition> page = bookDefinitionRepository.findAll(spec, pageable);

        List<BookByBookDefinitionResponseDto> items = page.getContent().stream()
                .map(BookByBookDefinitionResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.BOOK_DEFINITION, page);

        PaginationResponseDto<BookByBookDefinitionResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public PaginationResponseDto<BookForReaderResponseDto> getBooksForUser(PaginationFullRequestDto requestDto, Long categoryGroupId, Long categoryId, Long authorId) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.BOOK_DEFINITION);

        Specification<BookDefinition> spec = Specification.where(baseFilterBookDefinitions(requestDto.getKeyword(), requestDto.getSearchBy(), requestDto.getActiveFlag()))
                .and(filterByCategoryId(categoryId))
                .and(filterByCategoryGroupId(categoryGroupId))
                .and(filterByAuthorId(authorId))
                .and(filterByBooksCountGreaterThanZero());

        Page<BookDefinition> page = bookDefinitionRepository.findAll(spec, pageable);

        List<BookForReaderResponseDto> items = page.getContent().stream()
                .map(BookForReaderResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.BOOK_DEFINITION, page);

        PaginationResponseDto<BookForReaderResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public BookDetailForReaderResponseDto getBookDetailForUser(Long id) {
        BookDefinition bookDefinition = findEntityById(id);
        if (bookDefinition.getBooks().isEmpty()) {
            throw new NotFoundException(ErrorMessage.BookDefinition.ERR_NOT_FOUND_ID, id);
        }

        return new BookDetailForReaderResponseDto(bookDefinition);
    }

    @Override
    public PaginationResponseDto<BookForReaderResponseDto> advancedSearchBooks(List<QueryFilter> queryFilters, PaginationSortRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.BOOK_DEFINITION);

        Specification<BookDefinition> spec = filterByBooksCountGreaterThanZero()
                .and(BookSpecification.getSpecificationFromFilters(queryFilters));

        Page<BookDefinition> page = bookDefinitionRepository.findAll(spec, pageable);

        List<BookForReaderResponseDto> items = page.getContent().stream()
                .map(BookForReaderResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.BOOK_DEFINITION, page);

        PaginationResponseDto<BookForReaderResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public PaginationResponseDto<BookForReaderResponseDto> searchBooks(BookDefinitionFilter filters, PaginationSortRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.BOOK_DEFINITION);

        Specification<BookDefinition> spec = filterByBooksCountGreaterThanZero()
                .and(BookSpecification.filterBooks(filters));

        Page<BookDefinition> page = bookDefinitionRepository.findAll(spec, pageable);

        List<BookForReaderResponseDto> items = page.getContent().stream()
                .map(BookForReaderResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.BOOK_DEFINITION, page);

        PaginationResponseDto<BookForReaderResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public byte[] getBooksPdfContent(Set<Long> ids) {
        List<Book> books = bookRepository.findAllByBookDefinitionIdIn(ids);
        return pdfService.createPdfFromBooks(books);
    }

    @Override
    public byte[] getBooksLabelType1PdfContent(Set<Long> ids) {
        List<Book> books = bookRepository.findAllByBookDefinitionIdIn(ids);
        return pdfService.createLabelType1Pdf(books);
    }

    @Override
    public byte[] getBooksLabelType2PdfContent(Set<Long> ids) {
        List<Book> books = bookRepository.findAllByBookDefinitionIdIn(ids);
        return pdfService.createLabelType2Pdf(books);
    }

    @Override
    public byte[] generateBookListPdf() {
        List<Book> books = bookRepository.findAll();
        return pdfService.createBookListPdf(books);
    }
}