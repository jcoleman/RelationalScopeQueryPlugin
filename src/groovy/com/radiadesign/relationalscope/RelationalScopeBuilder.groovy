package com.radiadesign.relationalscope

class RelationalScopeBuilder {
  
  RelationalScope scope
  
  RelationalScopeBuilder(Class domain) {
    scope = new RelationalScope(domain)
  }
  
  def methodMissing(String name, args) {
    
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