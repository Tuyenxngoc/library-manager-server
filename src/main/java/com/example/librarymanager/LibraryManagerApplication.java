package com.example.librarymanager;

import com.example.librarymanager.config.CloudinaryConfig;
import com.example.librarymanager.config.MailConfig;
import com.example.librarymanager.config.properties.AdminInfo;
import com.example.librarymanager.domain.entity.UserGroup;
import com.example.librarymanager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
@EnableConfigurationProperties({
        AdminInfo.class,
        MailConfig.class,
        CloudinaryConfig.class
})
@EnableScheduling
public class LibraryManagerApplication {

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

    public static void main(String[] args) {
        Environment env = SpringApplication.run(LibraryManagerApplication.class, args).getEnvironment();
        String appName = env.getProperty("spring.application.name");
        if (appName != null) {
            appName = appName.toUpperCase();
        }
        String port = env.getProperty("server.port");
        log.info("-------------------------START {} Application------------------------------", appName);
        log.info("   Application         : {}", appName);
        log.info("   Url swagger-ui      : http://localhost:{}/swagger-ui.html", port);
        log.info("-------------------------START SUCCESS {} Application----------------------", appName);
    }

    @Bean
    CommandLineRunner init(AdminInfo adminInfo) {
        return args -> {
            roleService.initRoles();
            UserGroup userGroup = userGroupService.initUserGroup();
            userService.initAdmin(adminInfo, userGroup);
            readerService.initReadersFromCsv(readersCsvPath);
            authorService.initAuthorsFromCsv(authorsCsvPath);
            bookSetService.initBookSetsFromCSv(bookSetsCsvPath);
            categoryGroupService.initCategoryGroupsFromCsv(categoryGroupsCsvPath);
            categoryService.initCategoriesFromCsv(categoriesCsvPath);
            publisherService.initPublishersFromCsv(publishersCsvPath);
            classificationSymbolService.initClassificationSymbolsFromCsv(classificationSymbolsCsvPath);
            bookDefinitionService.initBookDefinitionsFromCsv(bookDefinitionsCsvPath);
        };
    }

}
