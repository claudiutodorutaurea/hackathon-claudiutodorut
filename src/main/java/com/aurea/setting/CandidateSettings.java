package com.aurea.setting;

import lombok.Data;

@Data
public class CandidateSettings {
    private int hourPerDay;
    private int devTime;
    private int focusScore;
    private int intensityScore;
    private int deepWorkBlock;
    private int workBlocksLess;
    private String date;
}
