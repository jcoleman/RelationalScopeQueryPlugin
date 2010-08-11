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
  
  Scenario: Query using 'in' subquerying
    Given I have the following domain class:
      """
      class Book {
        String title
        String author
      }
      """
    Given I have the following domain class:
      """
      class Author {
        String name
      }
      """
    And I have created the following "Book" instances:
      | title                       | author     |
      | Lord of the Rings           | Tolkien    |
      | 20000 Leagues Under the Sea | Verne      |
    When I execute the following code:
      """
      Book.where {
        author 'in': Author.where { }.select { property("name") }
      }.all()
      """
    Then I should get the following results:
      | title |
    Given I have created the following "Author" instances:
      | name     |
      | Tolkien  |
      | Verne    |
    When I execute the following code:
      """
      Book.where {
        author 'in': Author.where {
          name equals: "Tolkien"
        }.select { property("name") }
      }.all()
      """
    Then I should get the following results:
      | title              |
      | Lord of the Rings  |