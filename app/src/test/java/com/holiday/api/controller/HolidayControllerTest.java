package com.holiday.api.controller;

import com.holiday.api.controller.HolidayController;
import com.holiday.api.domain.Holiday;
import com.holiday.api.request.CountryRequest;
import com.holiday.api.service.HolidayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * The type Holiday controller test.
 */
@ExtendWith(MockitoExtension.class)
class HolidayControllerTest {

    @InjectMocks
    private HolidayController holidayController;

    @Mock
    private HolidayService holidayService;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test get past holidays positive case.
     */
    @Test
    void testGetPastHolidays() {
        String countryCode = "US";
        List<Holiday> mockHolidays = Arrays.asList(
                new Holiday(LocalDate.now().minusDays(10), Map.of("US", "Thanksgiving")),
                new Holiday(LocalDate.now().minusDays(20), Map.of("US", "Veterans Day"))
        );

        when(holidayService.getPastHolidays(countryCode)).thenReturn(mockHolidays);

        ResponseEntity<List<Holiday>> response = holidayController.getPastHolidays(
                countryCode);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    /**
     * Test get past holidays no holidays.
     */
    @Test
    void testGetPastHolidays_NoHolidays() {
        String countryCode = "US";
        when(holidayService.getPastHolidays(countryCode)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Holiday>> response = holidayController.getPastHolidays(
                countryCode);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    /**
     * Test get holidays count positive case.
     */
    @Test
    void testGetHolidaysCount() {
        CountryRequest request = new CountryRequest(2024, List.of("US", "CA"));
        Map<String, Long> mockResponse = Map.of("US", 2L, "CA", 1L);

        when(holidayService.getHolidaysCount(request)).thenReturn(mockResponse);

        ResponseEntity<Map<String, Long>> response = holidayController.getHolidaysCount(
                request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().get("US"));
        assertEquals(1, response.getBody().get("CA"));
    }

    /**
     * Test get common holidays positive case.
     */
    @Test
    void testGetCommonHolidays() {
        CountryRequest request = new CountryRequest(2024, List.of("US", "CA"));
        List<Holiday> mockHolidays = Collections.singletonList(
                new Holiday(LocalDate.of(2024, 1, 1), Map.of(
                        "US", "New Year", "CA", "New Year"))
        );

        when(holidayService.getCommonHolidays(request)).thenReturn(mockHolidays);

        ResponseEntity<List<Holiday>> response = holidayController.getCommonHolidays(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }


    /**
     * Test get common holidays with invalid country codes.
     */
    @Test
    void testGetCommonHolidays_InvalidCountryCodes() {
        CountryRequest request = new CountryRequest(2024, List.of("XX", "YY"));
        when(holidayService.getCommonHolidays(request)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Holiday>> response = holidayController.getCommonHolidays(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    /**
     * Test get past holidays with invalid country code.
     */
    @Test
    void testGetPastHolidays_InvalidCountryCode() {
        String invalidCountryCode = "XX";
        when(holidayService.getPastHolidays(invalidCountryCode)).thenReturn(
                Collections.emptyList());

        ResponseEntity<List<Holiday>> response = holidayController.getPastHolidays(
                invalidCountryCode);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    /**
     * Test get common holidays with service exception.
     */
    @Test
    void testGetCommonHolidays_ServiceException() {
        CountryRequest request = new CountryRequest(2024, List.of("US", "CA"));
        when(holidayService.getCommonHolidays(request)).thenThrow(
                new RuntimeException("Service error"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                holidayController.getCommonHolidays(request));
        assertEquals("Service error", exception.getMessage());
    }
}
