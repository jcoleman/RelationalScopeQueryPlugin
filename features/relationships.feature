Feature: Relationship queries
  In order to perform more advanced queries than allowed by GORM
  As a developer
  I want to query relationships using scopes
  
  Scenario: Query for children
    Given I have the following domain class:
      """
      class MarriedPerson {
        String name
        String gender
        MarriedPerson spouse
        Integer age
        
        static constraints = {
          gender(nullable: true)
          spouse(nullable: true)
        }
      }
      """
    And I have created the following "MarriedPerson" graph:
      """
        [
          {
            "name": "Harold",
            "gender": "male",
            "age": 18,
            "spouse": {
              "class": "MarriedPerson",
              "name": "Maude"
            }
          },
          {
            "name": "Maude",
            "gender": "female",
            "age": 57
          },
          {
            "name": "Jeff",
            "gender": "male",
            "age": 26,
            "spouse": {
              "class": "MarriedPerson",
              "name": "Nancy"
            }
          },
          {
            "name": "Nancy",
            "gender": "female",
            "age": 24,
            "spouse": {
              "class": "MarriedPerson",
              "name": "Jeff"
            }
          }
        ]
      """
    When I execute the following code:
      """
      MarriedPerson.where {
        spouse where: {
          age gte: 25
        }
      }.all()
      """
    Then I should get the following results:
      | name    |
      | Nancy   |
      | Harold  |
  
  Scenario: Query for children
    Given I have created the following "MarriedPerson" graph:
      """
        [
          {
            "name": "Harold",
            "gender": "male",
            "age": 18,
            "spouse": {
              "class": "MarriedPerson",
              "name": "Maude"
            }
          },
          {
            "name": "Maude",
            "gender": "female",
            "age": 57
          }
        ]
      """
    When I execute the following code:
      """
      MarriedPerson.where {
        spouse in: [MarriedPerson.findByName('Maude')]
      }.all()
      """
    Then I should get the following results:
      | name    |
      | Harold  |
  
  Scenario: Query using 'in' list of objects from Domain.load()
    Given I have created the following "MarriedPerson" graph:
      """
        [
          {
            "name": "Harold",
            "gender": "male",
            "age": 18,
            "spouse": {
              "class": "MarriedPerson",
              "name": "Maude"
            }
          },
          {
            "name": "Maude",
            "gender": "female",
            "age": 57
          }
        ]
      """
    When I execute the following code:
      """
      def mId
      MarriedPerson.withNewSession {
        mId = MarriedPerson.findByName('Maude').id
      }
      MarriedPerson.where {
        spouse in: [MarriedPerson.load(mId)]
      }.all()
      """
    Then I should get the following results:
      | name    |
      | Harold  |
  
  Scenario: Query using a mapped property
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
              "name": "Maude"
            }
          },
          {
            "name": "Maude",
            "gender": "female"
          },
          {
            "name": "Jeff",
            "gender": "male",
            "bestFriend": {
              "class": "FriendlyPerson",
              "name": "Harold"
            }
          },
          {
            "name": "Nancy",
            "gender": "female",
            "bestFriend": {
              "class": "FriendlyPerson",
              "name": "Maude"
            }
          }
        ]
      """
    When I execute the following code:
      """
      FriendlyPerson.where {
        gender mapTo: "friend_gender"
        bestFriend where: {
          gender equals: mapping("friend_gender")
        }
      }.all()
      """
    Then I should get the following results:
      | name    |
      | Nancy   |
      | Jeff    |
  
  Scenario: Query using a ancestor property
    Given I have created the following "FriendlyPerson" graph:
      """
        [
          {
            "name": "Harold",
            "gender": "male",
            "bestFriend": {
              "class": "FriendlyPerson",
              "name": "Maude"
            }
          },
          {
            "name": "Maude",
            "gender": "female"
          },
          {
            "name": "Jeff",
            "gender": "male",
            "bestFriend": {
              "class": "FriendlyPerson",
              "name": "Harold"
            }
          },
          {
            "name": "Nancy",
            "gender": "female",
            "bestFriend": {
              "class": "FriendlyPerson",
              "name": "Maude"
            }
          }
        ]
      """
    When I execute the following code:
      """
      FriendlyPerson.where {
        // We're going to access the 'gender' property later on...but without a mapping.
        bestFriend where: {
          gender equals: property('../gender')
        }
      }.all()
      """
    Then I should get the following results:
      | name    |
      | Nancy   |
      | Jeff    |
    When I execute the following code:
      """
      FriendlyPerson.where {
        bestFriend where: {
          // Test a round-about way of getting to a local property...
          gender equals: property('../bestFriend.gender')
        }
      }.all()
      """
    Then I should get the following results:
      | name    |
      | Nancy   |
      | Jeff    |
      | Harold  |
  
  Scenario: Query for children
    Given I have created the following "MarriedPerson" graph:
      """
        [
          {
            "name": "Harold",
            "gender": "male",
            "age": 18,
            "spouse": {
              "class": "MarriedPerson",
              "name": "Maude"
            }
          },
          {
            "name": "Maude",
            "gender": "female",
            "age": 57
          },
          {
            "name": "Jeff",
            "gender": "male",
            "age": 26,
            "spouse": {
              "class": "MarriedPerson",
              "name": "Nancy"
            }
          },
          {
            "name": "Nancy",
            "gender": "female",
            "age": 24,
            "spouse": {
              "class": "MarriedPerson",
              "name": "Jeff"
            }
          }
        ]
      """
    When I execute the following code:
      """
      MarriedPerson.where( [
        {
          name is: notNull
        },
        {
          spouse where: {
            age gte: 25
          }
        },
        {
          name is: notNull
        },
        {
          spouse where: {
            age is: notNull
          }
        }
      ] ).all()
      """
    Then I should get the following results:
      | name    |
      | Nancy   |
      | Harold  |