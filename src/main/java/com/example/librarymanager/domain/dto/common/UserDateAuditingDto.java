package com.example.librarymanager.domain.dto.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class UserDateAuditingDto extends DateAuditingDto {

    protected String createdBy;

    protected String lastModifiedBy;

}
