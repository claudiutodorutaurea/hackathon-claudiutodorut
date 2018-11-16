package com.aurea;

import com.aurea.service.SheetService;
import com.aurea.setting.CandidateSettings;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class Application implements CommandLineRunner {

    private final transient SheetService sheetService;
    private final transient CandidateSettings settings;

    public Application(final SheetService sheetService, final CandidateSettings settings) {
        this.sheetService = sheetService;
        this.settings = settings;
    }

    public static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class).web(WebApplicationType.NONE).run(args);
    }

    @Override
    public void run(final String... args) {
        sheetService.writeContentSheet(settings.getDate());
    }

}
