package com.radiadesign.relationalscope.comparison

class ScopeComparisonFactory {
  
  private ScopeComparisonFactory() { }
  
  static equals(String propertyName, value) {
    new EqualsScopeComparison (propertyName, value)
  }
  
  static is(String propertyName, value) {
    new IsScopeComparison (propertyName, value)
  }
  
}