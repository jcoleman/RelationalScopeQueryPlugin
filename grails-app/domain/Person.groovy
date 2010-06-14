class Person {
  String name
  String gender
  Person parent
  
  static constraints = {
    gender(nullable: true)
    parent(nullable: true)
  }
}