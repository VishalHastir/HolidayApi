package com.holiday.api.cucumber.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

/**
 * The type Holiday steps.
 */
public class HolidaySteps {

    private Response response;

    /**
     * The holiday api is running.
     */
    @Given("the holiday API is running")
    public void theHolidayApiIsRunning() {
        RestAssured.baseURI = "http://localhost:8084/holidays";
    }

    /**
     * Request past holidays for.
     *
     * @param countryCode the country code
     */
    @When("I request past holidays for {string}")
    public void iRequestPastHolidaysFor(String countryCode) {
        response = given().when().get("/past/" + countryCode);
    }

    /**
     * Should receive a list of past holidays.
     */
    @Then("I should receive a list of past holidays")
    public void iShouldReceiveAListOfPastHolidays() {
        response.then().statusCode(HttpStatus.OK.value())
                .body("$", not(empty()));
    }

    /**
     * Should receive an error response with status.
     *
     * @param statusCode the status code
     */
    @Then("I should receive an error response with status {int}")
    public void iShouldReceiveAnErrorResponseWithStatus(int statusCode) {
        response.then().statusCode(statusCode);
    }

    /**
     * Request the holiday count for the following countries.
     *
     * @param table the table
     */
    @When("I request the holiday count for the following countries")
    public void iRequestTheHolidayCountForTheFollowingCountries(DataTable table) {
        List<String> countryCodes = table.asMaps(String.class, String.class).stream()
                .map(row -> row.get("countryCode"))
                .collect(Collectors.toList());

        response = given().contentType("application/json")
                .body(Map.of("year", 2024, "countryCodes", countryCodes))
                .when().post("/count");
    }

    /**
     * Should receive holiday count for each country.
     */
    @Then("I should receive holiday count for each country")
    public void iShouldReceiveHolidayCountForEachCountry() {
        response.then().statusCode(HttpStatus.OK.value())
                .body("$", not(empty()));
    }

    /**
     * Request common holidays for and.
     *
     * @param country1 the country 1
     * @param country2 the country 2
     */
    @When("I request common holidays for {string} and {string}")
    public void iRequestCommonHolidaysForAnd(String country1, String country2) {
        response = given().contentType("application/json")
                .body(Map.of("year", 2024, "countryCodes", List.of(country1, country2)))
                .when().post("/common");
    }

    /**
     * Should receive a list of common holidays.
     */
    @Then("I should receive a list of common holidays")
    public void iShouldReceiveAListOfCommonHolidays() {
        response.then().statusCode(HttpStatus.OK.value())
                .body("$", not(empty()));
    }

    /**
     * Request holiday count with an empty country list.
     */
    @When("I request holiday count with an empty country list")
    public void iRequestHolidayCountWithAnEmptyCountryList() {
        response = given().contentType("application/json")
                .body(Map.of("year", 2024, "countryCodes", List.of()))
                .when().post("/count");
    }

    /**
     * Request common holidays for only one country.
     *
     * @param country the country
     */
    @When("I request common holidays for only one country {string}")
    public void iRequestCommonHolidaysForOnlyOneCountry(String country) {
        response = given().contentType("application/json")
                .body(Map.of("year", 2024, "countryCodes", List.of(country)))
                .when().post("/common");
    }
}
