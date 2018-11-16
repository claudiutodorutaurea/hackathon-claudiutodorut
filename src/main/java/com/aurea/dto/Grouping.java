package com.aurea.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Grouping {
    private Double alignmentScore;
    private Double focusScore;
    private Double intensityScore;
    private AdvancedGroups[] advancedGroups;
}
