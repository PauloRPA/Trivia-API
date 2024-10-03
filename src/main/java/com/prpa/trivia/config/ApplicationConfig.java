package com.prpa.trivia.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    @Autowired
    public void configureDelegatingMessageSource(DelegatingMessageSource delegatingMessageSource) {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("bundles.exceptions", "bundles.messages");
        messageSource.setUseCodeAsDefaultMessage(true);

        delegatingMessageSource.setParentMessageSource(messageSource);
    }

}
