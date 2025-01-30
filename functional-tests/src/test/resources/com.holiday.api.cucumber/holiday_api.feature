Feature: Holiday API Functional Tests

  Scenario: Get past holidays for a valid country code
    Given the holiday API is running
    When I request past holidays for "US"
    Then I should receive a list of past holidays

  Scenario: Get past holidays for an invalid country code
    Given the holiday API is running
    When I request past holidays for "XYZ"
    Then I should receive an error response with status 404

  Scenario: Get holidays count for multiple countries
    Given the holiday API is running
    When I request the holiday count for the following countries
      | countryCode |
      | US          |
      | CA          |
    Then I should receive holiday count for each country

  Scenario: Get common holidays between two countries
    Given the holiday API is running
    When I request common holidays for "US" and "CA"
    Then I should receive a list of common holidays

  Scenario: Request holidays count with an empty list
    Given the holiday API is running
    When I request holiday count with an empty country list
    Then I should receive an error response with status 400

  Scenario: Request common holidays with one country
    Given the holiday API is running
    When I request common holidays for only one country "US"
    Then I should receive an error response with status 400
