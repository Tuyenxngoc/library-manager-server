package com.example.librarymanager.domain.dto.response.category;

import com.example.librarymanager.domain.entity.CategoryGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryGroupTree {

    @Getter
    private static class Category {
        private final long id;
        private final String name;
        private final int count;

        public Category(com.example.librarymanager.domain.entity.Category category) {
            this.id = category.getId();
            this.name = category.getCategoryName();
            this.count = category.getBookDefinitions().size();
        }
    }

    private final long id;

    private final String name;

    private final int count;

    private final List<Category> categories;

    public CategoryGroupTree(CategoryGroup categoryGroup) {
        this.id = categoryGroup.getId();
        this.name = categoryGroup.getGroupName();
        this.categories = categoryGroup.getCategories().stream()
                .map(Category::new)
                .toList();

        this.count = categories.stream()
                .mapToInt(Category::getCount)
                .sum();
    }
}
