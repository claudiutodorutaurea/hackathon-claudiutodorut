package com.aurea.service;

import com.aurea.client.HttpRestTemplate;
import com.aurea.dto.CheckInChat;
import com.aurea.setting.CrossOverSettings;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
class DefaultCheckInChatService implements CheckInChatService {

    private final transient HttpRestTemplate restTemplate;
    private final transient CrossOverSettings settings;

    DefaultCheckInChatService(final HttpRestTemplate restTemplate, final CrossOverSettings settings) {
        this.restTemplate = restTemplate;
        this.settings = settings;
    }
    
    @Override
    public CheckInChat[] getCheckInChat(final long teamId, final String fromDate, final String toDate) {
        return restTemplate.getResponse(settings.getCheckInChatApi(), HttpMethod.GET, null, CheckInChat[].class, fromDate, toDate, teamId)
                .getBody();
    }

}
