package com.example.librarymanager.config;

import com.example.librarymanager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
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

    @Value("${data.authors.csv}")
    private String authorsCsvPath;

    @Value("${data.bookSets.csv}")
    private String bookSetsCsvPath;

    @Value("${data.bookDefinitions.csv}")
    private String bookDefinitionsCsvPath;

    @Value("${data.categoryGroups.csv}")
    private String categoryGroupsCsvPath;

    @Value("${data.categories.csv}")
    private String categoriesCsvPath;

    @Value("${data.classificationSymbols.csv}")
    private String classificationSymbolsCsvPath;

    @Value("${data.publishers.csv}")
    private String publishersCsvPath;

    @Value("${data.readers.csv}")
    private String readersCsvPath;

    @Override
    public void run(String... args) {
        roleService.init();
        userGroupService.init();
        userService.init();

        ExecutorService executor = Executors.newFixedThreadPool(6);

        List<CompletableFuture<Void>> tasks = List.of(
                CompletableFuture.runAsync(() -> readerService.init(readersCsvPath), executor),
                CompletableFuture.runAsync(() -> authorService.init(authorsCsvPath), executor),
                CompletableFuture.runAsync(() -> bookSetService.init(bookSetsCsvPath), executor),
                CompletableFuture.runAsync(() -> categoryGroupService.init(categoryGroupsCsvPath), executor),
                CompletableFuture.runAsync(() -> publisherService.init(publishersCsvPath), executor),
                CompletableFuture.runAsync(() -> classificationSymbolService.init(classificationSymbolsCsvPath), executor)
        );

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

        categoryService.init(categoriesCsvPath);
        bookDefinitionService.init(bookDefinitionsCsvPath);

        executor.shutdown();
    }

}
