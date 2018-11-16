package com.aurea.service;

import com.aurea.dto.CheckInChat;

public interface CheckInChatService {

    CheckInChat[] getCheckInChat(long teamId, String fromDate, String toDate);
}
