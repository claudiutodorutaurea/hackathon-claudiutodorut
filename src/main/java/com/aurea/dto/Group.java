package com.aurea.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Group {
    private Grouping grouping;
    private AssignmentHistory assignmentHistory;
    private Assignment assignment;
    private DayActivitiesTime dayActivitiesTime;
}
