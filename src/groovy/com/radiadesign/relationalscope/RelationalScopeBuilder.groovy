package com.radiadesign.relationalscope

class RelationalScopeBuilder {
  
  RelationalScope scope
  
  RelationalScopeBuilder(RelationalScope _scope) {
    scope = _scope
  }
  
  def methodMissing(String name, args) {
    
  }
  
  def where(arg) {
    scope.addScopeOrComparison( new RelationalScope().where(arg) )
  }
  
  def or(arg) {
    scope.addScopeOrComparison( new OrRelationalScope().where(arg) )
  }
  
  def and(arg) {
    scope.addScopeOrComparison( new RelationalScope().where(arg) )
  }
  
  def not(arg) {
    scope.addScopeOrComparison( new NotRelationalScope().where(arg) )
  }
   
  // This returns an object upon which may be called any of the supported comparison
  // operations. That call is expected to modify <this.scope>.
  def propertyMissing(String name) {
    new ComparisonScopeMutator(scope, name)
  }
  
  def getRelationalScope() {
    return scope
  }
  
}