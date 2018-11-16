package com.aurea.dto;

import lombok.Data;

@Data
public class CheckInChat {
    private long id;
    private String date;
    private String status;
    private String comment;
    private Boolean unblocked;
    private long assignmentId;
}
