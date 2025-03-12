package com.example.librarymanager;

import com.example.librarymanager.config.CloudinaryConfig;
import com.example.librarymanager.config.MailConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@Log4j2
@RequiredArgsConstructor
@SpringBootApplication
@EnableConfigurationProperties({MailConfig.class, CloudinaryConfig.class})
@EnableScheduling
public class LibraryManagerApplication {

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

}
