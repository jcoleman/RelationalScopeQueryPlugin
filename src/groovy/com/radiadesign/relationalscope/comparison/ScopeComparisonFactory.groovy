package com.radiadesign.relationalscope.comparison

class ScopeComparisonFactory {
  
  private ScopeComparisonFactory() { }
  
  static equals(String propertyName, value) {
    new EqualsScopeComparison(propertyName, value)
  }
  
  static is(String propertyName, value) {
    new IsScopeComparison(propertyName, value)
  }
  
  static lt(String propertyName, value) {
    new LessThanScopeComparison(propertyName, value)
  }
  
  static lte(String propertyName, value) {
    new LessThanOrEqualScopeComparison(propertyName, value)
  }
  
  static gt(String propertyName, value) {
    new GreaterThanScopeComparison(propertyName, value)
  }
  
  static gte(String propertyName, value) {
    new GreaterThanOrEqualScopeComparison(propertyName, value)
  }
  
  static 'in'(String propertyName, value) {
    new InScopeComparison(propertyName, value)
  }
  
  static mapTo(String propertyName, value) {
    new PropertyMappingScopeComparison(propertyName, value)
  }
  
}