package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*

class LikeScopeComparison extends ScopeComparisonBase {
  
  LikeScopeComparison(_lhsValue, _rhsValue) {
    super(_lhsValue, _rhsValue)
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    Restrictions.like(property, value)
  }
  
  String toString() {
    return "(${lhsValue} LIKE ${rhsValue})"
  }
  
}