package com.radiadesign.relationalscope.junction

import org.hibernate.criterion.*

class ScopeJunctionBase {
  
  def relationalScopes
  
  def getJunction() {
    assert false : "Please implement this method in inheriting classes"
  }
  
  ScopeJunctionBase(scopes) {
    relationalScopes = scopes
  }
  
  Criterion toCriterion() {
    relationalScopes.inject(this.junction) { criterion, scope ->
      criterion.add(scope.toCriterion())
    }
  }
  
  String toString() {
    
  }
  
}