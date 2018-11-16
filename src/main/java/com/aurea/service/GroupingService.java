package com.aurea.service;

import com.aurea.dto.Group;

public interface GroupingService {
    
    Group[] getGrouping(long teamId, String date);
}
