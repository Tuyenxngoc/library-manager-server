package com.example.librarymanager.domain.dto.request;

import com.example.librarymanager.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDefinitionRequestDto {
    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String title;  // Nhan đề

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String bookCode; // Kí hiệu tên sách

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Long categoryId; // Danh mục

    private Long publisherId; // Nhà xuất bản

    private Long bookSetId; // Bộ sách

    private Long classificationSymbolId; // Kí hiệu phân loại

    private Integer pageCount; // Số trang

    private Double price; // Giá bán

    private Double referencePrice; // Giá tham khảo

    @NotNull(message = ErrorMessage.INVALID_ARRAY_IS_REQUIRED)
    private Set<@NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED) Long> authorIds = new HashSet<>();// Tác giả

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String publicationPlace; // Nơi xuất bản

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String publishingYear; // Năm xuất bản

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String edition; // Lần xuất bản

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String bookSize; // Khổ sách (cm)

    @Size(max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String parallelTitle; // Nhan đề song song

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String subtitle; // Phụ đề

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String additionalMaterial; // Tài liệu đi kèm

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String summary; // Tóm tắt

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String isbn; // Mã ISBN

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String keywords; // Từ khóa tìm kiếm

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String language; // Ngôn ngữ

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String additionalInfo; // Thông tin khác

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String series; // Tùng thư

    @URL(message = ErrorMessage.INVALID_URL_FORMAT)
    private String imageUrl; // Ảnh bìa

}
