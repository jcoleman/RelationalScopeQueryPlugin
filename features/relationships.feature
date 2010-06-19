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
        spouse.where {
          age gte: 25
        }
      }.all()
      """
    Then I should get the following results:
      | name    |
      | Nancy   |
      | Harold  |