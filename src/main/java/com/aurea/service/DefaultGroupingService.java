package com.aurea.service;

import com.aurea.client.HttpRestTemplate;
import com.aurea.dto.Group;
import com.aurea.setting.CrossOverSettings;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
class DefaultGroupingService implements GroupingService {
    private final transient HttpRestTemplate restTemplate;
    private final transient CrossOverSettings settings;

    DefaultGroupingService(final HttpRestTemplate restTemplate, final CrossOverSettings settings) {
        this.restTemplate = restTemplate;
        this.settings = settings;
    }

    @Override
    public Group[] getGrouping(final long teamId, final String date) {
        return restTemplate.getResponse(settings.getTrackerApi(), HttpMethod.GET, null, Group[].class, date, teamId).getBody();
    }

}
