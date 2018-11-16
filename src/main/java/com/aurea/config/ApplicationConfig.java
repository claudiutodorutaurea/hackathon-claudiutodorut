package com.aurea.config;

import com.aurea.setting.CandidateSettings;
import com.aurea.setting.CrossOverSettings;
import com.aurea.setting.GoogleSettings;
import com.aurea.setting.Hackathon;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(basePackages = {"com.aurea"})
@EnableConfigurationProperties(Hackathon.class)
public class ApplicationConfig {

    private final transient Hackathon application;
    
    public ApplicationConfig(final Hackathon application) {
        this.application= application;
    }
    
    @Bean
    public CrossOverSettings crossOverSettings() {
        return application.getCrossOver();
    }

    @Bean
    public CandidateSettings candidateSettings() {
        return application.getCandidate();
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public GoogleSettings googleSettings() {
        return application.getGoogle();
    }


}
