package com.prpa.trivia.config;

import com.prpa.trivia.model.trivia.Category;
import com.prpa.trivia.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    @Autowired
    public void configureDelegatingMessageSource(DelegatingMessageSource delegatingMessageSource) {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("bundles.exceptions", "bundles.messages");

        delegatingMessageSource.setParentMessageSource(messageSource);
    }

    @Bean
    @ConditionalOnResource(resources = "/sql/data/category.csv")
    @ConditionalOnProperty("application.data.fill.category")
    public CommandLineRunner fillCategoryDB(@Autowired CategoryRepository categoryRepository) {
        return (args -> {
            InputStream categoriesCSV = ApplicationConfig.class.getResourceAsStream("/sql/data/category.csv");
            csvToList(categoriesCSV).stream()
                    .map((name) -> new Category(null, name))
                    .forEach(categoryRepository::save);
        });
    }

    private List<String> csvToList(InputStream inputStream) {
        return csvToList(inputStream, ",");
    }

    private List<String> csvToList(InputStream inputStream, String delimiter) {
        if (inputStream == null) return List.of();
        List<String> itemList = new ArrayList<>();
        Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter(delimiter);

        for (String item = scanner.next(); scanner.hasNext(); item = scanner.next()) {
            itemList.add(item.trim());
        }
        scanner.close();
        return itemList;
    }

}
