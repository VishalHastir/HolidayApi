package com.holiday.api.controller;

import com.holiday.api.domain.Holiday;
import com.holiday.api.request.CountryRequest;
import com.holiday.api.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * The type Holiday controller.
 */
@RestController
@RequestMapping("/holidays")
public class HolidayController {

    /**
     * The Holiday service.
     */
    @Autowired
    HolidayService holidayService;

    /**
     * Gets last 3 holidays.
     *
     * @param countryCode the countrycode
     * @return the last 3 holidays
     */
    @GetMapping("/past/{countryCode}")
    public ResponseEntity<List<Holiday>> getPastHolidays(@PathVariable String countryCode) {

        List<Holiday> last3Holidays = holidayService.getPastHolidays(countryCode);
        return ResponseEntity.ok(last3Holidays);
    }


    /**
     * Gets holidays count.
     *
     * @param countryRequest the country request
     * @return the holidays count
     */
    @PostMapping("/count")
    public ResponseEntity<Map<String, Long>> getHolidaysCount(@RequestBody CountryRequest countryRequest) {
        List<String> countryCodes = countryRequest.getCountryCodes();
        if (countryCodes.isEmpty()) {
            throw new IllegalArgumentException("At least one country code is required to find holidays count.");
        }
        Map<String, Long> holidaysCount = holidayService.getHolidaysCount(countryRequest);
        return ResponseEntity.ok(holidaysCount);
    }


    /**
     * Gets common holidays.
     *
     * @param countryRequest the country request
     * @return the common holidays
     */
    @PostMapping("/common")
    public ResponseEntity<List<Holiday>> getCommonHolidays(@RequestBody CountryRequest countryRequest) {
        List<String> countryCodes = countryRequest.getCountryCodes();
        if (countryCodes.isEmpty() || countryCodes.size() < 2) {
            throw new IllegalArgumentException("At least two country codes are required to find common holidays.");
        }

        List<Holiday> commonHolidays = holidayService.getCommonHolidays(countryRequest);
        return ResponseEntity.ok(commonHolidays);
    }
}
