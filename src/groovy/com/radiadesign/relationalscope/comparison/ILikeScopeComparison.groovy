package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*

class ILikeScopeComparison extends ScopeComparisonBase {
  
  ILikeScopeComparison(_lhsValue, _rhsValue) {
    super(_lhsValue, _rhsValue)
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    Restrictions.ilike(property, value)
  }
  
  String toString() {
    return "(${lhsValue} LIKE ${rhsValue})"
  }
  
}