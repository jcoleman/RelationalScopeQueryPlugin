Feature: Arithmetic queries
  In order to perform more advanced queries than allowed by GORM
  As a developer
  I want to query using arithemetic expressions
  
  Scenario: Query with basic subtraction eq (expression and value)
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
  
  Scenario: Query with 'chained' arithmetic eq (subtracting expression and value)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 2    |
      | 4    |
    When I execute the code "NumberDomain.where { ((property('x') - 1) - 1) eq: 0 }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
  
  Scenario: Addition eq (expression and value)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 2    |
      | 4    |
    When I execute the code "NumberDomain.where { (property('x') + 1) eq: 3 }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
  
  Scenario: Multiplication eq (expression and value)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 2    |
      | 4    |
    When I execute the code "NumberDomain.where { (property('x') * 3) eq: 6 }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
  
  Scenario: Division eq (expression and value)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 2    |
      | 4    |
    When I execute the code "NumberDomain.where { (property('x') / 2) eq: 1 }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
  
  Scenario: Subtraction eq (value and expression)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 1    |
      | 2    |
    When I execute the code "NumberDomain.where { value(0) eq: property('x') - 1 }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
  
  Scenario: Subtraction eq (expression and expression)
    Given I have created the following "NumberDomain" instances:
      | x    | y   |
      | 1    | 1   |
      | 2    | 3   |
    When I execute the code "NumberDomain.where { (property('y') - 1) eq: property('x') - 1 }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
  
  Scenario: Subtraction eq (property and expression)
    Given I have created the following "NumberDomain" instances:
      | x    | y   |
      | 1    | 0   |
      | 2    | 4   |
    When I execute the code "NumberDomain.where { property('y') eq: property('x') - 1 }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
  
  Scenario: Subtraction eq (expression and property)
    Given I have created the following "NumberDomain" instances:
      | x    | y   |
      | 1    | 0   |
      | 2    | 4   |
    When I execute the code "NumberDomain.where { (property('x') - 1) eq: property('y') }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
  
  # The following test currently works (that is, it generates the correct SQL)
  # however, there is a bug in the HSQLDB that is used in testing that causes
  # the following error to be thrown:
  #   Unresolved parameter type : as both operands of aritmetic operator in statement
  #   [select this_.id as id25_0_, this_.version as version25_0_, this_.x as x25_0_,
  #   this_.y as y25_0_ from number_domain this_ where ( ?  -  ?  = this_.x -  ? )]
  # See: https://issues.apache.org/jira/browse/CAY-990
  # ----
  #Scenario: Subtraction (expression and expression)
  #  Given I have created the following "NumberDomain" instances:
  #    | x    |
  #    | 1    |
  #    | 2    |
  #  When I execute the code "NumberDomain.where { (value(2) - value(2)) eq: property('x') - 1 }.all()"
  #  Then I should get the following results:
  #    | x    |
  #    | 1    |
  
  Scenario: Addition gte (expression and value)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 1    |
      | 2    |
      | 3    |
    When I execute the code "NumberDomain.where { (property('x') + 1) gte: 3 }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
      | 3    |
  
  Scenario: Addition gt (expression and value)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 1    |
      | 2    |
      | 3    |
    When I execute the code "NumberDomain.where { (property('x') + 1) gt: 3 }.all()"
    Then I should get the following results:
      | x    |
      | 3    |
  
  Scenario: Addition lte (expression and value)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 1    |
      | 2    |
      | 3    |
    When I execute the code "NumberDomain.where { (property('x') + 1) lte: 3 }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
      | 2    |
  
  Scenario: Addition lt (expression and value)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 1    |
      | 2    |
      | 3    |
    When I execute the code "NumberDomain.where { (property('x') + 1) lt: 3 }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
  
  Scenario: Addition ne (expression and value)
    Given I have created the following "NumberDomain" instances:
      | x    |
      | 1    |
      | 2    |
      | 3    |
    When I execute the code "NumberDomain.where { (property('x') + 1) ne: 3 }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
      | 3    |
  
  Scenario: Addition ne (expression and value) *with nulls*
    Given I have created the following "NumberDomain" instances:
      | x    | y    |
      | 1    | 1    |
      | 2    | 2    |
      | 3    |      |
    When I execute the code "NumberDomain.where { (property('y') + value(1)) ne: value(3) }.all()"
    # 2 + 1 == 3, so result x=2 shouldn't be included
    # in db, null + 1 == null, but in our logic, null != 3 so result x=3 should be included
    Then I should get the following results:
      | x    |
      | 1    |
      | 3    |
  
  Scenario: Addition is null (expression and value)
    Given I have created the following "NumberDomain" instances:
      | x    | y    |
      | 1    | 1    |
      | 2    |      |
    When I execute the code "NumberDomain.where { (property('y') + value(1)) is: null }.all()"
    Then I should get the following results:
      | x    |
      | 2    |
  
  Scenario: Addition is not null (expression and value)
    Given I have created the following "NumberDomain" instances:
      | x    | y    |
      | 1    | 1    |
      | 2    |      |
    When I execute the code "NumberDomain.where { (property('y') + value(1)) is: notNull }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
  
  Scenario: In (property, expression list)
    Given I have created the following "NumberDomain" instances:
      | x    | y   |
      | 1    | 0   |
      | 2    | 0   |
      | 3    | 0   |
    When I execute the code "NumberDomain.where { x in: [property('y') + 1, property('y') + 3] }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
      | 3    |
  
  Scenario: In (expression, literal list)
    Given I have created the following "NumberDomain" instances:
      | x    | y   |
      | 1    | 3   |
      | 2    | 2   |
      | 3    | 1   |
    When I execute the code "NumberDomain.where { (property('x') + 1) in: [2, 3] }.all()"
    Then I should get the following results:
      | x    |
      | 1    |
      | 2    |
  