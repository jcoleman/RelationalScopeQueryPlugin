Feature: Arithmetic queries
  In order to perform more advanced queries than allowed by GORM
  As a developer
  I want to query using arithemetic expressions
  
  Scenario: Query with basic subtraction (property and value)
    Given I have the following domain class:
      """
      class NumberDomain {
        BigInteger x
        BigInteger y
        
        static constraints = {
          y nullable: true
        }
      }
      """
    And I have created the following "NumberDomain" instances:
      | x    |
      | 1    |
      | 2    |
    When I execute the code "NumberDomain.where { (property('x') - 1) eq: 0 }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
  
  Scenario: Query with 'chained' arithmetic (subtracting property and value)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 2    |
      | 4    |
    When I execute the code "NumberDomain.where { ((property('x') - 1) - 1) eq: 0 }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
  
  Scenario: Addition (property and value)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 2    |
      | 4    |
    When I execute the code "NumberDomain.where { (property('x') + 1) eq: 3 }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
  
  Scenario: Multiplication (property and value)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 2    |
      | 4    |
    When I execute the code "NumberDomain.where { (property('x') * 3) eq: 6 }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
  
  Scenario: Division (property and value)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 2    |
      | 4    |
    When I execute the code "NumberDomain.where { (property('x') / 2) eq: 1 }.all()"
    Then I should get the following results:
      | x    |
      | 2    |