package com.radiadesign.relationalscope.comparison

class ScopeComparisonFactory {
  
  private ScopeComparisonFactory() { }
  
  static equals(String propertyName, value) {
    new EqScopeComparison (propertyName, value)
  }
  
}