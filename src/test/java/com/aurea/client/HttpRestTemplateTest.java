package com.aurea.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.aurea.BaseMockitoTest;
import com.aurea.dto.Group;
import com.aurea.setting.CrossOverSettings;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HttpRestTemplateTest extends BaseMockitoTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CrossOverSettings settings;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void getResponse() {
        // Arrange
        final HttpRestTemplate httpRestTemplate = new HttpRestTemplate(restTemplate, settings);
        final String uriVariables = "variable1";
        final String url = "url";
        final String body = "body";
        final ResponseEntity<Group[]> value = mock(ResponseEntity.class);
        when(restTemplate.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.eq(Group[].class), Mockito.eq(uriVariables))).thenReturn(value);
        
        // Act
        final ResponseEntity<Group[]> response = httpRestTemplate.getResponse(url, HttpMethod.GET, body, Group[].class,
                uriVariables);
        
        // Assert
        softly.assertThat(response).isEqualTo(value);
    }
}
