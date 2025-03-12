package com.example.librarymanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleService roleService;
    private final UserGroupService userGroupService;
    private final UserService userService;
    private final ReaderService readerService;
    private final AuthorService authorService;
    private final BookSetService bookSetService;
    private final BookDefinitionService bookDefinitionService;
    private final CategoryGroupService categoryGroupService;
    private final CategoryService categoryService;
    private final PublisherService publisherService;
    private final ClassificationSymbolService classificationSymbolService;

    @Value("${data.users.csv}")
    private String usersCsvPath;
    @Value("${data.authors.csv}")
    private String authorsCsvPath;
    @Value("${data.booksets.csv}")
    private String bookSetsCsvPath;
    @Value("${data.bookDefinitions.csv}")
    private String bookDefinitionsCsvPath;
    @Value("${data.categorygroups.csv}")
    private String categoryGroupsCsvPath;
    @Value("${data.categories.csv}")
    private String categoriesCsvPath;
    @Value("${data.classificationsymbols.csv}")
    private String classificationSymbolsCsvPath;
    @Value("${data.publishers.csv}")
    private String publishersCsvPath;
    @Value("${data.readers.csv}")
    private String readersCsvPath;

    @Override
    public void run(String... args) throws Exception {
        roleService.init();
        userGroupService.init();
        userService.init(usersCsvPath);
        readerService.init(readersCsvPath);
        authorService.init(authorsCsvPath);
        bookSetService.init(bookSetsCsvPath);
        categoryGroupService.init(categoryGroupsCsvPath);
        categoryService.init(categoriesCsvPath);
        publisherService.init(publishersCsvPath);
        classificationSymbolService.init(classificationSymbolsCsvPath);
        bookDefinitionService.init(bookDefinitionsCsvPath);
    }

}
