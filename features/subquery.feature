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
        String authorName
        Author author
        
        static constraints = {
          author nullable: true
        }
      }
      """
    Given I have the following domain class:
      """
      class Author {
        String name
      }
      """
    And I have created the following "Book" instances:
      | title                       | authorName |
      | Lord of the Rings           | Tolkien    |
      | 20000 Leagues Under the Sea | Verne      |
    When I execute the following code:
      """
      Book.where {
        authorName 'in': Author.where { }.select { property("name") }
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
        authorName 'in': Author.where {
          name equals: "Tolkien"
        }.select { property("name") }
      }.all()
      """
    Then I should get the following results:
      | title              |
      | Lord of the Rings  |
  
  Scenario: Query with property('../propName') walking inside an exists subquery
    Given I have created the following "Book" instances:
      | title                       | authorName |
      | Lord of the Rings           | Tolkien    |
      | 20000 Leagues Under the Sea | Verne      |
    And I have created the following "Author" instances:
      | name     |
      | Tolkien  |
      | Verne    |
    When I execute the following code:
      """
      Book.where {
        exists(
          Author.where {
            name equals: property('../author.name')
          }
        )
      }.all()
      """
    Then I should get the following results:
      | title              |
    When I execute the following code:
      """
      def book = Book.findByTitle('Lord of the Rings')
      book.author = Author.findByName('Tolkien')
      book.save()
      """
    And I execute the following code:
      """
      Book.where {
        exists(
          Author.where {
            name equals: property('../author.name')
          }
        )
      }.all()
      """
    Then I should get the following results:
      | title              |
      | Lord of the Rings  |
  
  Scenario: Entering an association within an exists subquery (bug had been created by new property walking code)
    Given I have created the following "Book" instances:
      | title                       | authorName |
      | Lord of the Rings           | Tolkien    |
      | 20000 Leagues Under the Sea | Verne      |
    And I have created the following "Author" instances:
      | name      |
      | Tolkien   |
      | Verne     |
      | Bernstein |
    When I execute the following code:
      """
      Author.where {
        name mapTo: 'author_name'
        exists(
          Book.where {
            author where: {
              name equals: mapping('author_name')
            }
          }
        )
      }.all()
      """
    Then I should get the following results:
      | name     |
    When I execute the following code:
      """
      def book = Book.findByTitle('Lord of the Rings')
      book.author = Author.findByName('Tolkien')
      book.save()
      """
    And I execute the following code:
      """
      Author.where {
        name mapTo: 'author_name'
        exists(
          Book.where {
            author where: {
              name equals: mapping('author_name')
            }
          }
        )
      }.all()
      """
  
  Scenario: Using both property('../propName') walking and mapping for the same association property at the same time (bug: query exception due to duplicate alias being created had been created by new property walking code)
    Given I have created the following "Book" instances:
      | title                       | authorName |
      | Lord of the Rings           | Tolkien    |
      | 20000 Leagues Under the Sea | Verne      |
    And I have created the following "Author" instances:
      | name      |
      | Tolkien   |
      | Verne     |
      | Bernstein |
    When I execute the following code:
      """
      Book.where {
        id mapTo: 'book_id'
        author is: notNull
        author where: {
          name mapTo: 'author_name'
        }
        exists(
          Book.where {
            id equals: mapping('book_id')
            property('../author.name') ne: 'Bernstein'
            author where: {
              name equals: mapping('author_name')
            }
          }
        )
      }.all()
      """
    Then I should get the following results:
      | name     |
    When I execute the following code:
      """
      def book = Book.findByTitle('Lord of the Rings')
      book.author = Author.findByName('Tolkien')
      book.save()
      """
    And I execute the following code:
      """
      Book.where {
        id mapTo: 'book_id'
        author is: notNull
        author where: {
          name mapTo: 'author_name'
        }
        exists(
          Book.where {
            id equals: mapping('book_id')
            property('../author.name') ne: 'Bernstein'
            author where: {
              name equals: mapping('author_name')
            }
          }
        )
      }.all()
      """
    Then I should get the following results:
      | title             |
      | Lord of the Rings |
  
  Scenario: Query with property('../propName') walking inside a subquery
    Given I have created the following "Book" instances:
      | title                       | authorName |
      | Lord of the Rings           | Tolkien    |
      | 20000 Leagues Under the Sea | Verne      |
    And I have created the following "Author" instances:
      | name     |
      | Tolkien  |
      | Verne    |
    When I execute the following code:
      """
      Book.where {
        authorName equals: Author.where {
          name equals: property('../authorName')
        }.select(['name':'name'])
      }.all()
      """
    Then I should get the following results:
      | title                       |
      | Lord of the Rings           |
      | 20000 Leagues Under the Sea |
  
  Scenario: Subquery
    Given I have created the following "Book" instances:
      | title                       | authorName |
      | Lord of the Rings           | Tolkien    |
      | 20000 Leagues Under the Sea | Verne      |
    And I have created the following "Author" instances:
      | name     |
      | Tolkien  |
      | Verne    |
    When I execute the following code:
      """
      Book.where {
        authorName equals: Author.where {
          name equals: 'Tolkien'
        }.select(['name':'name'])
      }.all()
      """
    Then I should get the following results:
      | title             |
      | Lord of the Rings |
  
  Scenario: Subquery
    Given I have created the following "Book" instances:
      | title                              | authorName |
      | Lord of the Rings                  | Tolkien    |
      | 20000 Leagues Under the Sea        | Verne      |
      | Journey to the Center of the Earth | Verne      |
    And I have created the following "Author" instances:
      | name     |
      | Tolkien  |
      | Verne    |
    When I execute the following code:
      """
      Author.where {
        name in: Book.where {
          authorName equals: 'Verne'
        }.select(authorName: 'author.name')
      }.all()
      """
    Then I should get the following results:
      | name   |
    When I execute the following code:
      """
      def book = Book.findByTitle('Journey to the Center of the Earth')
      book.author = Author.findByName('Verne')
      book.save()
      """
    And I execute the following code:
      """
      Author.where {
        name in: Book.where {
          authorName equals: 'Verne'
        }.select(authorName: 'author.name')
      }.all()
      """
    Then I should get the following results:
      | name   |
      | Verne  |
  
  