Feature: Queries selecting specific properties and turning the results into a list of maps.
  In order to perform more advanced queries than allowed by GORM
  As a developer
  I want to query for a specific subset of fields using scopes
  
  Scenario: Select first-level properties
    Given I have the following domain class:
      """
      class FriendlyPerson {
        String name
        String gender
        FriendlyPerson bestFriend
      
        static constraints = {
          gender(nullable: true)
          bestFriend(nullable: true)
        }
      }
      """
    And I have created the following "FriendlyPerson" graph:
      """
        [
          {
            "name": "Harold",
            "gender": "male",
            "bestFriend": {
              "class": "FriendlyPerson",
              "name": "Jeff"
            }
          },
          {
            "name": "Jeff",
            "gender": "male",
            "bestFriend": {
              "class": "FriendlyPerson",
              "name": "Nancy"
            }
          },
          {
            "name": "Nancy",
            "gender": "female",
            "bestFriend": {
              "class": "FriendlyPerson",
              "name": "Jeff"
            }
          }
        ]
      """
    When I execute the following code:
      """
      FriendlyPerson.where {
        // No restrictions for this test
      }.select(
        "person.name":"name"
      ).all()
      """
    Then I should get exactly the following result maps:
      | person.name |
      | Harold      |
      | Jeff        |
      | Nancy       |
    When I execute the following code:
      """
      FriendlyPerson.where {
        // No restrictions for this test
      }.select(
        "person.name":"name",
        "person.gender":"gender"
      ).all()
      """
    Then I should get exactly the following result maps:
      | person.name | person.gender |
      | Harold      | male          |
      | Jeff        | male          |
      | Nancy       | female        |
    When I execute the following code:
      """
      FriendlyPerson.where {
        // No restrictions for this test
      }.select(
        "person.name":"name",
        "person.gender": { property("gender") }
      ).all()
      """
    Then I should get exactly the following result maps:
      | person.name | person.gender |
      | Harold      | male          |
      | Jeff        | male          |
      | Nancy       | female        |
    When I execute the following code:
      """
      FriendlyPerson.where {
        // Provide a "hint" to relscope to make the join by querying on the deep property
        bestFriend where: {
          name is: notNull
        }
      }.select(
        "test": { distinct("gender") }
      ).all()
      """
    Then I should get exactly the following result maps:
      | test   |
      | male   |
      | female |
  
  Scenario: Select deep properties
    Given I have created the following "FriendlyPerson" graph:
      """
        [
          {
            "name": "Harold",
            "gender": "male",
            "bestFriend": {
              "class": "FriendlyPerson",
              "name": "Jeff"
            }
          },
          {
            "name": "Jeff",
            "gender": "male",
            "bestFriend": {
              "class": "FriendlyPerson",
              "name": "Nancy"
            }
          },
          {
            "name": "Nancy",
            "gender": "female",
            "bestFriend": {
              "class": "FriendlyPerson",
              "name": "Jeff"
            }
          }
        ]
      """
    When I execute the following code:
      """
      FriendlyPerson.where {
        // Provide a "hint" to relscope to make the join by querying on the deep property
        bestFriend where: {
          name is: notNull
        }
      }.select(
        "name":"bestFriend.name"
      ).all()
      """
    Then I should get exactly the following result maps:
      | name   |
      | Jeff   |
      | Nancy  |
      | Jeff   |
    When I execute the following code:
      """
      FriendlyPerson.where {
        // Don't provide any hints for joining (should work either way)
      }.select(
        "name":"bestFriend.name"
      ).all()
      """
    Then I should get exactly the following result maps:
      | name   |
      | Jeff   |
      | Nancy  |
      | Jeff   |
    When I execute the following code:
      """
      FriendlyPerson.where {
        // Don't provide any hints for joining (should work either way)
      }.select(
        "name":"bestFriend.bestFriend.name"
      ).all()
      """
    Then I should get exactly the following result maps:
      | name   |
      | Nancy  |
      | Jeff   |
      | Nancy  |
  
  Scenario: Query with grouping mapped selection of aggregrate values
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
      | x   | y   |
      | 1   | 2   |
      | 1   | 4   |
      | 2   | 4   |
      | 2   | 8   |
    When I execute the following code:
      """
      NumberDomain.where {
        // No restrictions
      }.select(
        x: { groupBy("x") },
        "y_average": { average("y") }
      ).all()
      """
    Then I should get the following results:
      | x    | y_average |
      | 1    | 3         |
      | 2    | 6         |