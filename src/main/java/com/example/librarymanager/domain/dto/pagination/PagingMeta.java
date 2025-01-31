package com.example.librarymanager.domain.dto.pagination;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PagingMeta {

    private Long totalElements;

    private Integer totalPages;

    private Integer pageNum;

    private Integer pageSize;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sortBy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sortType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String keyword;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String searchBy;

}