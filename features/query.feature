Feature: Basic queries
  In order to perform more advanced queries than allowed by GORM
  As a developer
  I want to query using scopes
  
  Scenario: Query for all instances
    Given I have the following domain class:
      """
      class Person {
        String name
        String gender
        
        static constraints = {
          gender(nullable: true)
        }
      }
      """
    And I have created the following "Person" instances:
      | name    |
      | Gregory |
      | Harold  |
      | Alice   |
    When I execute the code "Person.list()"
    Then I should get the following results:
      | name    |
      | Gregory |
      | Harold  |
      | Alice   |
      
  Scenario: Conveniently query by domain identifier by accessing the domain like a list
    Given I have the following domain class:
      """
      class Person {
        String name
        String gender

        static constraints = {
          gender(nullable: true)
        }
      }
      """
    And I have created the following "Person" instances:
      | name    |
      | Gregory |
    When I execute the code "[Person[(Person.list() as ArrayList)[-1]?.id]]"
    Then I should get the following results:
      | name    |
      | Gregory |
    
  Scenario: Query by a single property
    Given I have created the following "Person" instances:
      | name    |
      | Gregory |
      | Harold  |
      | Alice   |
    When I execute the following code:
      """
      Person.where { name equals: "Gregory" }.all()
      """
    Then I should get the following results:
      | name    |
      | Gregory |
    
  Scenario: Query with a scope
    Given I have created the following "Person" instances:
      | name    | gender |
      | Gregory | male   |
      | Harold  | male   |
      | Alice   | female |
    When I execute the following code:
      """
      def men = Person.where { gender equals: "male" }
      men.all()
      """
    Then I should get the following results:
      | name    |
      | Gregory |
      | Harold  |
  
  Scenario: Query with "or" clause
    Given I have created the following "Person" instances:
      | name    | gender |
      | Gregory | male   |
      | Harold  | male   |
      | Alice   | female |
    When I execute the following code:
      """
      Person.where {
        or {
          name equals: "Harold"
          name equals: "Alice"
        }
      }.all()
      """
    Then I should get the following results:
      | name    |
      | Harold  |
      | Alice   |