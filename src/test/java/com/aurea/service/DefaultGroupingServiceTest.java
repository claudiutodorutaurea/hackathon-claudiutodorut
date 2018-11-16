package com.aurea.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.aurea.BaseMockitoTest;
import com.aurea.client.HttpRestTemplate;
import com.aurea.dto.Group;
import com.aurea.setting.CrossOverSettings;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class DefaultGroupingServiceTest extends BaseMockitoTest {

    @Mock
    private HttpRestTemplate restTemplate;

    @Mock
    private CrossOverSettings settings;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void getResponse() {
        // Arrange
        final GroupingService groupingService = new DefaultGroupingService(restTemplate, settings);
        when(settings.getTrackerApi()).thenReturn("url");
        final ResponseEntity<Group[]> entity = mock(ResponseEntity.class);
        final Group[] value = new Group[1];
        value[0] = mock(Group.class);
        when(entity.getBody()).thenReturn(value);
        final String date = "date";
        final long teamId = 1L;
        when(restTemplate.getResponse(Mockito.eq(settings.getTrackerApi()), Mockito.eq(HttpMethod.GET),
                Mockito.eq(null), Mockito.eq(Group[].class), Mockito.eq(date), Mockito.eq(teamId))).thenReturn(entity);
        
        // Act
        final Group[] response = groupingService.getGrouping(teamId, date);
        
        // Assert
        softly.assertThat(response).isEqualTo(value);
    }
}
