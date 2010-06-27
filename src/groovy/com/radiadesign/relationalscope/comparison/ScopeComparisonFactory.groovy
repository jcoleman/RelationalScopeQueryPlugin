package com.radiadesign.relationalscope.comparison

class ScopeComparisonFactory {
  
  private ScopeComparisonFactory() { }
  
  static equals(lhs, rhs) {
    new EqualsScopeComparison(lhs, rhs)
  }
  
  static is(lhs, rhs) {
    new IsScopeComparison(lhs, rhs)
  }
  
  static lt(lhs, rhs) {
    new LessThanScopeComparison(lhs, rhs)
  }
  
  static lte(lhs, rhs) {
    new LessThanOrEqualScopeComparison(lhs, rhs)
  }
  
  static gt(lhs, rhs) {
    new GreaterThanScopeComparison(lhs, rhs)
  }
  
  static gte(lhs, rhs) {
    new GreaterThanOrEqualScopeComparison(lhs, rhs)
  }
  
  static 'in'(lhs, rhs) {
    new InScopeComparison(lhs, rhs)
  }
  
  static mapTo(property, value) {
    new PropertyMappingScopeComparison(property, value)
  }
  
}