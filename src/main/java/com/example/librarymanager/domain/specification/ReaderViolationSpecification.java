package com.example.librarymanager.domain.specification;

import com.example.librarymanager.constant.PenaltyForm;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.domain.entity.ReaderViolation;
import com.example.librarymanager.domain.entity.ReaderViolation_;
import com.example.librarymanager.domain.entity.Reader_;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class ReaderViolationSpecification {

    public static Specification<ReaderViolation> filterReaderViolations(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Reader_.CARD_NUMBER -> {
                        Join<ReaderViolation, Reader> readerJoin = root.join(ReaderViolation_.reader);
                        predicate = builder.and(predicate,
                                builder.like(readerJoin.get(Reader_.cardNumber), "%" + keyword + "%"));
                    }

                    case Reader_.FULL_NAME -> {
                        Join<ReaderViolation, Reader> readerJoin = root.join(ReaderViolation_.reader);
                        predicate = builder.and(predicate,
                                builder.like(readerJoin.get(Reader_.fullName), "%" + keyword + "%"));
                    }

                    case ReaderViolation_.PENALTY_FORM -> {
                        try {
                            PenaltyForm penaltyForm = PenaltyForm.valueOf(keyword.toUpperCase());
                            predicate = builder.and(predicate, builder.equal(root.get(ReaderViolation_.penaltyForm), penaltyForm));
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
            }

            return predicate;
        };
    }

}
