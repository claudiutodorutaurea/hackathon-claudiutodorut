package com.aurea.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Assignment {
    private long id;
    private Candidate candidate;
    private Manager manager;
}
