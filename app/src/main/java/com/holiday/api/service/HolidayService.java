package com.holiday.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.holiday.api.constants.HolidayConstants;
import com.holiday.api.domain.Holiday;
import com.holiday.api.request.CountryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The type Holiday service.
 */
@Service
@Slf4j
public class HolidayService {

    @Value("${holiday.api.url}")
    private String apiUrl;

    /**
     * Gets last 3 holidays.
     *
     * @param country the country
     * @return the last 3 holidays
     */
    public List<Holiday> getPastHolidays(String country) {
        List<Holiday> pastHolidays = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        LocalDate today = LocalDate.now();
        int yearToCheck = currentYear;

        // Fetch holidays from current and previous years until we get at least 3 past holidays
        while (pastHolidays.size() < HolidayConstants.PREVIOUS_HOLIDAYS_COUNT) {
            List<Holiday> holidays = fetchHolidays(yearToCheck, country)
                    .stream()
                    .filter(holiday -> holiday.getDate().isBefore(today))
                    .sorted(Comparator.comparing(Holiday::getDate).reversed())
                    .collect(Collectors.toList());

            pastHolidays.addAll(holidays);

            yearToCheck--;

            if (yearToCheck < currentYear - 100) break;
        }

        return pastHolidays.stream().limit(HolidayConstants.PREVIOUS_HOLIDAYS_COUNT).collect(Collectors.toList());
    }

    /**
     * Gets holidays count.
     *
     * @param countryRequest the country request
     * @return the holidays count
     */
    public Map<String, Long> getHolidaysCount(CountryRequest countryRequest) {
        int year = countryRequest.getYear();
        return countryRequest.getCountryCodes().parallelStream()
                .collect(Collectors.toMap(
                        country -> country,
                        country -> fetchHolidays(year, country).stream()
                                .filter(h -> !isWeekend(h.getDate()))
                                .count(),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }


    /**
     * Gets common holidays.
     *
     * @param countryRequest the country request
     * @return the common holidays
     */
    public List<Holiday> getCommonHolidays(CountryRequest countryRequest) {
        int year = countryRequest.getYear();
        List<String> countryCodes = countryRequest.getCountryCodes();

        Map<LocalDate, Map<String, String>> holidayMap = new HashMap<>();

        // Fetch and process holidays for each country
        for (String countryCode : countryCodes) {
            List<Holiday> holidays = Optional.ofNullable(fetchHolidays(year, countryCode))
                    .orElse(Collections.emptyList());

            for (Holiday holiday : holidays) {
                holidayMap
                        .computeIfAbsent(holiday.getDate(), k -> new HashMap<>())
                        .put(countryCode, holiday.getLocalName());
            }
        }

        // Filter common holidays
        return holidayMap.entrySet().stream()
                .filter(entry -> entry.getValue().size() == countryCodes.size()) // Ensure the holiday appears in requested countries
                .sorted(Map.Entry.comparingByKey()) // Sort by date
                .map(entry -> new Holiday(entry.getKey(), entry.getValue())) // Create a Holiday object
                .collect(Collectors.toList());
    }


    /**
     * Fetch holidays list.
     *
     * @param year        the year
     * @param countryCode the country code
     * @return the list
     */
    public List<Holiday> fetchHolidays(int year, String countryCode) {
        String uri = apiUrl + "/" + year + "/" + countryCode;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching holidays: " + e.getMessage());
        }

        int statusCode = response.statusCode();
        String responseBody = response.body();

        return switch (statusCode) {
            case 200 -> parseHolidays(responseBody);
            case 404 ->
                    throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "No holidays found for " + countryCode + " in year " + year);
            case 400 -> throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, response.body());
            default ->
                    throw new HttpClientErrorException(HttpStatus.valueOf(response.statusCode()), "API request failed: " + response.uri().getPath() + " with " + response.statusCode());
        };
    }

    /**
     * Parses the API response JSON into a list of Holiday objects.
     */
    private List<Holiday> parseHolidays(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(responseBody, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Error parsing API response: " + e.getMessage(), e);
        }
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}
