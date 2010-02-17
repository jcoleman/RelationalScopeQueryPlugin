package com.radiadesign.relationalscope

import com.radiadesign.relationalscope.comparison.*

class RelationalScopeBuilder {
  
  RelationalScope scope
  
  RelationalScopeBuilder(RelationalScope _scope) {
    scope = _scope
  }
  
  def methodMissing(String name, args) {
    assert args.size() == 1 : "Setting comparisons for a property may only be called with a single map"
    args[0].each {
      scope.addScopeOrComparison(ScopeComparisonFactory."${it.key}"(name, it.value))
    }
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
  
  def getRelationalScope() {
    return scope
  }
  
}