package com.holiday.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * The type Holiday.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Holiday {

    private LocalDate date;
    private Map<String, String> localNames;
    @JsonProperty
    private String localName;

    /**
     * Instantiates a new Holiday.
     *
     * @param date       the date
     * @param localNames the local names
     */
    public Holiday(LocalDate date, Map<String, String> localNames) {
        this.date = date;
        this.localNames = localNames;
    }


}
