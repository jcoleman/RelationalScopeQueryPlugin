package com.radiadesign.relationalscope.comparison

class ScopeComparisonFactory {
  
  private ScopeComparisonFactory() { }
  
  static equals(property, value) {
    new EqualsScopeComparison(property, value)
  }
  
  static is(property, value) {
    new IsScopeComparison(property, value)
  }
  
  static lt(property, value) {
    new LessThanScopeComparison(property, value)
  }
  
  static lte(property, value) {
    new LessThanOrEqualScopeComparison(property, value)
  }
  
  static gt(property, value) {
    new GreaterThanScopeComparison(property, value)
  }
  
  static gte(property, value) {
    new GreaterThanOrEqualScopeComparison(property, value)
  }
  
  static 'in'(property, value) {
    new InScopeComparison(property, value)
  }
  
  static mapTo(property, value) {
    new PropertyMappingScopeComparison(property, value)
  }
  
}