Feature: Basic queries
  In order to perform more advanced queries than allowed by GORM
  As a developer
  I want to query using scopes
  
  Scenario: Query using lt property/value comparator in opposite order
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
    When I execute the code "NumberDomain.where { x gt: 1 as BigInteger }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
    When I execute the code "NumberDomain.where { value(1 as BigInteger) lt: property('x') }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
    
  Scenario: Query using gt property/value comparator in opposite order
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 1    |
      | 2    |
    When I execute the code "NumberDomain.where { x lt: 2 as BigInteger }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
    When I execute the code "NumberDomain.where { value(2 as BigInteger) gt: property('x') }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
    
  Scenario: Query using lte property/value comparator in opposite order
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 1    |
      | 2    |
      | 3    |
    When I execute the code "NumberDomain.where { x gte: 2 as BigInteger }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
      | 3    |
    When I execute the code "NumberDomain.where { value(2 as BigInteger) lte: property('x') }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
      | 3    |
    
  Scenario: Query using gte property/value comparator in opposite order
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 1    |
      | 2    |
      | 3    |
    When I execute the code "NumberDomain.where { x lte: 2 as BigInteger }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
      | 2    |
    When I execute the code "NumberDomain.where { value(2 as BigInteger) gte: property('x') }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
      | 2    |
  
  Scenario: Query using gte property/value comparator in opposite order
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 1    |
      | 2    |
      | 3    |
    When I execute the code "NumberDomain.where { x equals: 2 as BigInteger }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
    When I execute the code "NumberDomain.where { value(2 as BigInteger) equals: property('x') }.all()"
    Then I should get the following results:
      | x    |
      | 2    |