package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.dto.filter.LibraryVisitFilter;
import com.example.librarymanager.domain.entity.LibraryVisit;
import com.example.librarymanager.domain.entity.LibraryVisit_;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.domain.entity.Reader_;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LibraryVisitSpecification {

    public static Specification<LibraryVisit> filterLibraryVisits(String keyword, String searchBy, LibraryVisitFilter filter) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (filter == null || (filter.getStartDate() == null && filter.getEndDate() == null)) {
                LocalDate today = LocalDate.now();
                LocalDateTime startOfDay = today.atStartOfDay();
                LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

                // Thêm điều kiện lọc theo thời gian trong ngày hôm nay
                predicate = builder.and(predicate,
                        builder.between(root.get(LibraryVisit_.entryTime), startOfDay, endOfDay)
                );
            } else {
                if (filter.getStartDate() != null) {
                    predicate = builder.and(predicate,
                            builder.greaterThanOrEqualTo(root.get(LibraryVisit_.entryTime), filter.getStartDate().atStartOfDay()));
                }
                if (filter.getEndDate() != null) {
                    predicate = builder.and(predicate,
                            builder.lessThanOrEqualTo(root.get(LibraryVisit_.entryTime), filter.getEndDate().atTime(23, 59, 59)));
                }
            }

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case "cardNumber" -> {
                        Join<LibraryVisit, Reader> readerJoin = root.join(LibraryVisit_.reader);
                        predicate = builder.and(predicate, builder.like(readerJoin.get(Reader_.cardNumber), "%" + keyword + "%"));
                    }

                    case "fullName" -> {
                        Join<LibraryVisit, Reader> readerJoin = root.join(LibraryVisit_.reader);
                        predicate = builder.and(predicate, builder.like(readerJoin.get(Reader_.fullName), "%" + keyword + "%"));
                    }
                }
            }
            return predicate;
        };
    }

}
