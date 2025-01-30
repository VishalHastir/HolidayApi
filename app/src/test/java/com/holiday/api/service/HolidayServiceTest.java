package com.holiday.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.holiday.api.domain.Holiday;
import com.holiday.api.request.CountryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

/**
 * The type Holiday service test.
 */
@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    private final List<Holiday> sampleHolidays = Arrays.asList(
            new Holiday(LocalDate.of(2025, 12, 25), createHolidayMap(
                    "US", "Christmas Day")),
            new Holiday(LocalDate.of(2025, 1, 1), createHolidayMap(
                    "US", "New Year"))
    );
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private HolidayService holidayService;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Test get past holidays returns last three holidays.
     */
    @Test
    void testGetPastHolidays_ReturnsLastThreeHolidays() {
        HolidayService spyService = spy(holidayService);
        doReturn(sampleHolidays).when(spyService).fetchHolidays(anyInt(), anyString());

        List<Holiday> pastHolidays = spyService.getPastHolidays("US");

        assertThat(pastHolidays).hasSize(3);
        assertThat(pastHolidays.get(0).getDate()).isEqualTo(LocalDate.of(
                2025, 1, 1));
    }

    /**
     * Test get holidays count correct count per country.
     */
    @Test
    void testGetHolidaysCount_CorrectCountPerCountry() {
        HolidayService spyService = spy(holidayService);
        doReturn(sampleHolidays).when(spyService).fetchHolidays(anyInt(), anyString());

        Map<String, Long> holidayCount = spyService.getHolidaysCount(
                new CountryRequest(2025, List.of("US", "BR")));

        assertThat(holidayCount).containsEntry("US", 2L);
        assertThat(holidayCount).containsEntry("BR", 2L);
    }

    /**
     * Test get common holidays returns common holidays.
     */
    @Test
    void testGetCommonHolidays_ReturnsCommonHolidays() {
        HolidayService spyService = spy(holidayService);

        List<Holiday> usHolidays = Collections.singletonList(
                new Holiday(LocalDate.of(2025, 12, 25), Map.of(
                        "US", "Christmas Day"), "Christmas Day")
        );
        List<Holiday> brHolidays = Collections.singletonList(
                new Holiday(LocalDate.of(2025, 12, 25), Map.of(
                        "BR", "Natal"), "Natal")
        );

        doReturn(usHolidays).when(spyService).fetchHolidays(2025, "US");
        doReturn(brHolidays).when(spyService).fetchHolidays(2025, "BR");

        List<Holiday> commonHolidays = spyService.getCommonHolidays(
                new CountryRequest(2025, List.of("US", "BR")));

        assertThat(commonHolidays).hasSize(1);
        assertThat(commonHolidays.get(0).getLocalNames())
                .containsEntry("US", "Christmas Day")
                .containsEntry("BR", "Natal");
    }

    /**
     * Test get past holidays returns empty list when no past holidays.
     */
    @Test
    void testGetPastHolidays_ReturnsEmptyList_WhenNoPastHolidays() {
        HolidayService spyService = spy(holidayService);
        doReturn(Collections.emptyList()).when(spyService).fetchHolidays(
                anyInt(), anyString());

        List<Holiday> pastHolidays = spyService.getPastHolidays("US");

        assertThat(pastHolidays).isEmpty();
    }

    /**
     * Test get common holidays returns empty list when no common holidays.
     */
    @Test
    void testGetCommonHolidays_ReturnsEmptyList_WhenNoCommonHolidays() {
        HolidayService spyService = spy(holidayService);

        List<Holiday> usHolidays = List.of(new Holiday(
                LocalDate.of(2025, 12, 25), createHolidayMap(
                        "US", "Christmas Day")));
        List<Holiday> brHolidays = List.of(new Holiday(LocalDate.of(
                2025, 7, 4), createHolidayMap(
                        "BR", "Independence Day")));

        doReturn(usHolidays).when(spyService).fetchHolidays(2025, "US");
        doReturn(brHolidays).when(spyService).fetchHolidays(2025, "BR");

        List<Holiday> commonHolidays = spyService.getCommonHolidays(
                new CountryRequest(2025, List.of("US", "BR")));

        assertThat(commonHolidays).isEmpty();
    }

    /**
     * Test fetch holidays throws exception when api responds with 404.
     */
    @Test
    void testFetchHolidays_ThrowsException_WhenAPIRespondsWith404() {
        HolidayService spyService = spy(holidayService);
        doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "No holidays found"))
                .when(spyService).fetchHolidays(2025, "INVALID");

        assertThatThrownBy(() -> spyService.fetchHolidays(2025, "INVALID"))
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining("No holidays found");
    }

    /**
     * Test fetch holidays throws runtime exception on unknown error.
     */
    @Test
    void testFetchHolidays_ThrowsRuntimeException_OnUnknownError() {
        HolidayService spyService = spy(holidayService);
        doThrow(new RuntimeException("Unexpected error")).when(spyService).fetchHolidays(
                2025, "US");

        assertThatThrownBy(() -> spyService.fetchHolidays(2025, "US"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unexpected error");
    }

    // Helper method to create a map for holiday names
    private Map<String, String> createHolidayMap(String countryCode, String holidayName) {
        Map<String, String> holidayMap = new HashMap<>();
        holidayMap.put(countryCode, holidayName);
        return holidayMap;
    }
}
