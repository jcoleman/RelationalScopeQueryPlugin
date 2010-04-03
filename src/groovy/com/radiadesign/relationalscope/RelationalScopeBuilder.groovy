package com.radiadesign.relationalscope

import com.radiadesign.relationalscope.comparison.*

class RelationalScopeBuilder {
  
  RelationalScope scope
  
  def scopeStack
  
  RelationalScopeBuilder(RelationalScope _scope) {
    scope = _scope
  }
  
  def methodMissing(String name, args) {
    assert args.size() == 1 : "Setting comparisons for a property may only be called with a single map"
    args[0].each {
      addScopeOrComparisonToCurrentScope( ScopeComparisonFactory."${it.key}"(name, it.value) )
    }
  }
  
  def where(arg) {
    project( new RelationalScope(), arg )
  }
  
  def or(arg) {
    project( new OrRelationalScope(), arg )
  }
  
  def and(arg) {
    project( new RelationalScope(), arg )
  }
  
  def not(arg) {
    project( new NotRelationalScope(), arg )
  }
  
  def addScopeOrComparisonToCurrentScope(newScope) {
    if ( scopeStack && !scopeStack.empty() ) {
      scopeStack.peek().addScopeOrComparison(newScope)
    } else {
      scope.addScopeOrComparison(newScope)
    }
  }
  
  def project(newScope, Closure block) {
    if (!scopeStack) { scopeStack = new Stack() }
    scopeStack.push(newScope)
    
    block.call()
    
    addScopeOrComparisonToCurrentScope( scopeStack.pop() )
  }
  
  def project(newScope, RelationalScope additionalScope) {
    newScope.addScopeOrComparison(additionalScope)
    addScopeOrComparisonToCurrentScope(newScope)
  }
  
  // Helper property for 'is' comparison
  final notNull = 1
  
  def getRelationalScope() {
    return scope
  }
  
}