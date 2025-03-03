package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.ExportReceipt;
import com.example.librarymanager.domain.entity.ExportReceipt_;
import com.example.librarymanager.util.SpecificationsUtil;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class ExportReceiptSpecification {

    public static Specification<ExportReceipt> filterExportReceipts(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case ExportReceipt_.ID ->
                            predicate = builder.and(predicate, builder.equal(root.get(ExportReceipt_.ID),
                                    SpecificationsUtil.castToRequiredType(root.get(ExportReceipt_.id).getJavaType(), keyword)));

                    case ExportReceipt_.RECEIPT_NUMBER ->
                            predicate = builder.and(predicate, builder.like(root.get(ExportReceipt_.receiptNumber), "%" + keyword + "%"));

                    case ExportReceipt_.EXPORT_REASON ->
                            predicate = builder.and(predicate, builder.like(root.get(ExportReceipt_.exportReason), "%" + keyword + "%"));
                }
            }
            return predicate;
        };
    }

}
