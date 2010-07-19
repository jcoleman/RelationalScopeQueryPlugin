package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*

class BetweenScopeComparison extends ScopeComparisonBase {
  
  BetweenScopeComparison(_lhsValue, _rhsValue) {
    super(_lhsValue, _rhsValue)
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    if (value.size() != 2) {
      throw new RuntimeException("Between accepts a list of exactly 2 values.")
    }
    
    Restrictions.between(property, value[0], value[1])
  }
  
  String toString() {
    return "(${lhsValue} between ${rhsValue.join(' and ')})"
  }
  
}