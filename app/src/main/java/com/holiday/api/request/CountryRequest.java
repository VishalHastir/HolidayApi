package com.holiday.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The type Country request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryRequest {
    private int year;
    private List<String> countryCodes;
}
