package com.aurea.setting;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ConfigurationProperties(prefix = "application")
@ToString
public class Hackathon {
    
    private final CrossOverSettings crossOver = new CrossOverSettings();
    private final CandidateSettings candidate = new CandidateSettings();
    private final GoogleSettings google = new GoogleSettings();
}
