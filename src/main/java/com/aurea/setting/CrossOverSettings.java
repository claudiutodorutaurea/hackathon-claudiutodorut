package com.aurea.setting;

import lombok.Data;

@Data
public class CrossOverSettings {
    private String token;
    private long teamId;
    private String trackerApi;
    private String checkInChatApi;
}
