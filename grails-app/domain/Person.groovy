class Person {
  String name
  String gender
  
  static constraints = {
    gender(nullable: true)
  }
}