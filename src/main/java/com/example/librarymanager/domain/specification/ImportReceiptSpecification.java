package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.ImportReceipt;
import com.example.librarymanager.domain.entity.ImportReceipt_;
import com.example.librarymanager.util.SpecificationsUtil;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class ImportReceiptSpecification {

    public static Specification<ImportReceipt> filterImportReceipts(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case ImportReceipt_.ID ->
                            predicate = builder.and(predicate, builder.equal(root.get(ImportReceipt_.ID),
                                    SpecificationsUtil.castToRequiredType(root.get(ImportReceipt_.id).getJavaType(), keyword)));

                    case ImportReceipt_.RECEIPT_NUMBER ->
                            predicate = builder.and(predicate, builder.like(root.get(ImportReceipt_.receiptNumber), "%" + keyword + "%"));

                    case ImportReceipt_.FUNDING_SOURCE ->
                            predicate = builder.and(predicate, builder.like(root.get(ImportReceipt_.fundingSource), "%" + keyword + "%"));

                    case ImportReceipt_.IMPORT_REASON ->
                            predicate = builder.and(predicate, builder.like(root.get(ImportReceipt_.importReason), "%" + keyword + "%"));

                    case ImportReceipt_.GENERAL_RECORD_NUMBER ->
                            predicate = builder.and(predicate, builder.like(root.get(ImportReceipt_.generalRecordNumber), "%" + keyword + "%"));
                }
            }
            return predicate;
        };
    }

}
