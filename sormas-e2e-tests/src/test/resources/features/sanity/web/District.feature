@UI @Sanity @Configuration
Feature: District end to end tests

  @tmsLink=SORQA-343 @env_main @precon
  Scenario: Check infrastructure data for districts
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I click on Districts button in Configuration tab
    And I select country Germany
    Then I check that Voreingestellter Landkreis is correctly displayed

  @tmsLink=SORQA-344 @env_de @precon
  Scenario: Check German infrastructure data for districts
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I click on Districts button in Configuration tab
    And I select country Deutschland
    Then I check that Voreingestellter Landkreis is correctly displayed in German