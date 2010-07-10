Feature: Sub-queries
  In order to perform more advanced queries than allowed by GORM
  As a developer
  I want to query using scopes inside of other scopes as sub-queries
  
  Scenario: Query for instances where exists
    Given I have the following domain class:
      """
      class Customer {
        String name
      }
      """
    Given I have the following domain class:
      """
      class Account {
        String type
      }
      """
    And I have created the following "Customer" instances:
      | name    |
      | Bob     |
      | Elsie   |
    When I execute the code "Customer.where { exists( Account.where { } )}.all()"
    Then I should get the following results:
      | name    |
    And I have created the following "Account" instances:
      | type      |
      | Customer  |
    When I execute the code "Customer.where { exists( Account.where { } )}.all()"
    Then I should get the following results:
    | name    |
    | Bob     |
    | Elsie   |
    