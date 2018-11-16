package com.aurea.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.aurea.BaseMockitoTest;
import com.aurea.client.HttpRestTemplate;
import com.aurea.dto.CheckInChat;
import com.aurea.setting.CrossOverSettings;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class DefaultCheckInChatServiceTest extends BaseMockitoTest {

    @Mock
    private HttpRestTemplate restTemplate;

    @Mock
    private CrossOverSettings settings;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void getResponse() {
        // Arrange
        final CheckInChatService checkInChatService = new DefaultCheckInChatService(restTemplate, settings);
        when(settings.getCheckInChatApi()).thenReturn("url");
        final ResponseEntity<CheckInChat[]> entity = mock(ResponseEntity.class);
        final CheckInChat[] value = new CheckInChat[1];
        value[0] = mock(CheckInChat.class);
        final long teamId = 1L;
        final String fromDate = "fromDate";
        final String toDate = "toDate";
        when(entity.getBody()).thenReturn(value );
        when(restTemplate.getResponse(Mockito.eq(settings.getCheckInChatApi()), Mockito.eq(HttpMethod.GET),
                Mockito.eq(null), Mockito.eq(CheckInChat[].class), Mockito.eq(fromDate), Mockito.eq(toDate),
                Mockito.eq(teamId))).thenReturn(entity);
        
        // Act
        final CheckInChat[] response = checkInChatService.getCheckInChat(teamId, fromDate, toDate);
        
        // Assert
        softly.assertThat(response).isEqualTo(value);
    }
}
