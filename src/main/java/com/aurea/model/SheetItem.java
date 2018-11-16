package com.aurea.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.EqualsAndHashCode.Exclude;

@EqualsAndHashCode
@Builder
@Getter
public class SheetItem {
    private final long candidateId;
    
    @Exclude
    private final String candidateName;
    
    @Exclude
    private final String managerName;
    
    private final String date;
    
    @Exclude
    private final String hourPerDay;
    
    @Exclude
    private final String deepWorkBlock;
    
    @Exclude
    private final String devTime;
    
    @Exclude
    private final String dailyCic;
    
    @Exclude
    private final String intensityFocus;
    
    @Exclude
    private final int blockWorkLess;
    
    @Exclude
    private final String checkInChatCompliance;
}
