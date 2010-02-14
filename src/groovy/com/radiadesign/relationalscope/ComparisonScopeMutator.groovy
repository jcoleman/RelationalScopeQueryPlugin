package com.radiadesign.relationalscope

import com.radiadesign.relationalscope.comparison.*

class ComparisonScopeMutator {
  
  String propertyName
  RelationalScope owningScope
  
  ComparisonScopeMutator(RelationalScope mutatedScope, String _propertyName) {
    owningScope = mutatedScope
    propertyName = _propertyName
  }
  
  def eq(value) {
    owningScope.addScopeOrComparison(new EqScopeComparison (propertyName, value))
  }
  
}