package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.dto.filter.LogFilter;
import com.example.librarymanager.domain.entity.Log;
import com.example.librarymanager.domain.entity.Log_;
import com.example.librarymanager.domain.entity.User;
import com.example.librarymanager.domain.entity.User_;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class LogSpecification {

    public static Specification<Log> filterLogs(String keyword, String searchBy, LogFilter logFilter) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Log_.USER -> {
                        Join<Log, User> userJoin = root.join(Log_.user);
                        predicate = builder.and(predicate, builder.like(userJoin.get(User_.username), "%" + keyword + "%"));
                    }

                    case Log_.FEATURE ->
                            predicate = builder.and(predicate, builder.like(root.get(Log_.feature), "%" + keyword + "%"));

                    case Log_.EVENT ->
                            predicate = builder.and(predicate, builder.like(root.get(Log_.event), "%" + keyword + "%"));

                    case Log_.CONTENT ->
                            predicate = builder.and(predicate, builder.like(root.get(Log_.content), "%" + keyword + "%"));
                }
            }

            if (logFilter != null) {
                if (logFilter.getStartDate() != null) {
                    predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get(Log_.timestamp), logFilter.getStartDate().atStartOfDay()));
                }

                if (logFilter.getEndDate() != null) {
                    predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get(Log_.timestamp), logFilter.getEndDate().atTime(23, 59, 59)));
                }
            }

            return predicate;
        };
    }

}
