package com.aurea.util;

import java.util.Locale;
import java.util.Objects;

import com.aurea.dto.CheckInChat;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

@UtilityClass
public class Utils {
    private static final String YES = "Yes";
    private static final String NO = "No";
    private static final String BLOCKER = "blocker";
    private static final String TODAY = "today";
    private static final String YESTERDAY = "yesterday";
    private static final String NOT_DONE = "not_done";

    public static boolean containsString(final String comment, final String word) {
        return !StringUtils.isEmpty(comment) && comment.toLowerCase(Locale.getDefault()).contains(word);
    }

    public static String convertToYesNo(final boolean condition) {
        return condition ? YES : NO;
    }

    public boolean getCheckInChatCompliant(final CheckInChat[] checkInChats, final long assignmentId) {
        if (getCheckInChatStatus(checkInChats, assignmentId)) {
            for (final CheckInChat checkInChat : checkInChats) {
                if (checkInChat.getAssignmentId() == assignmentId) {
                    final String comment = checkInChat.getComment();
                    return Utils.containsString(comment, YESTERDAY) && Utils.containsString(comment, TODAY)
                            && Utils.containsString(comment, BLOCKER);
                }
            }
        }
        return false;
    }

    public boolean getCheckInChatStatus(final CheckInChat[] checkInChats, final long assignmentId) {
        for (final CheckInChat checkInChat : checkInChats) {
            if (checkInChat.getAssignmentId() == assignmentId) {
                final String status = checkInChat.getStatus();
                return !(Objects.nonNull(status) && status.toLowerCase(Locale.getDefault()).contains(NOT_DONE));
            }
        }
        return false;
    }
    
}
