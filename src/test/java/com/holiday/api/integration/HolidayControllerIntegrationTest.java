package com.holiday.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.holiday.api.domain.Holiday;
import com.holiday.api.request.CountryRequest;
import com.holiday.api.service.HolidayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class HolidayControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HolidayService holidayService;

    @BeforeEach
    void setUp() {
        reset(holidayService);
    }

    @Test
    void testGetPastHolidays_PositiveCase() throws Exception {
        String countryCode = "US";
        List<Holiday> holidays = List.of(
                new Holiday(LocalDate.now().minusDays(10), Map.of("US", "Thanksgiving")),
                new Holiday(LocalDate.now().minusDays(20), Map.of("US", "Veterans Day"))
        );

        when(holidayService.getPastHolidays(countryCode)).thenReturn(holidays);

        mockMvc.perform(get("/holidays/past/{countryCode}", countryCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetPastHolidays_NoHolidays() throws Exception {
        String countryCode = "US";
        when(holidayService.getPastHolidays(countryCode)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/holidays/past/{countryCode}", countryCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetHolidaysCount_PositiveCase() throws Exception {
        CountryRequest request = new CountryRequest(2024, List.of("US", "CA"));
        Map<String, Long> response = Map.of("US", 5L, "CA", 3L);

        when(holidayService.getHolidaysCount(request)).thenReturn(response);

        mockMvc.perform(post("/holidays/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.US").value(5))
                .andExpect(jsonPath("$.CA").value(3));
    }

    @Test
    void testGetHolidaysCount_InvalidRequest() throws Exception {
        CountryRequest request = new CountryRequest(2024, Collections.emptyList());

        mockMvc.perform(post("/holidays/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCommonHolidays_PositiveCase() throws Exception {
        CountryRequest request = new CountryRequest(2024, List.of("US", "CA"));
        List<Holiday> holidays = List.of(
                new Holiday(LocalDate.of(2024, 1, 1), Map.of("US", "New Year", "CA", "New Year"))
        );

        when(holidayService.getCommonHolidays(request)).thenReturn(holidays);

        mockMvc.perform(post("/holidays/common")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].localNames.US").value("New Year"))
                .andExpect(jsonPath("$[0].localNames.CA").value("New Year"));
    }

    @Test
    void testGetCommonHolidays_InvalidRequest() throws Exception {
        CountryRequest request = new CountryRequest(2024, Collections.emptyList());

        mockMvc.perform(post("/holidays/common")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
