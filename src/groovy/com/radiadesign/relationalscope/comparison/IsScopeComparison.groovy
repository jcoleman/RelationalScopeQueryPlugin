package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*

class IsScopeComparison extends ScopeComparisonBase {
  
  IsScopeComparison(_lhsValue, _rhsValue) {
    super(_lhsValue, _rhsValue)
  }
  
  Criterion criterionForPropertyAndProperty(lhs, rhs, options) {
    Restrictions.eqProperty(lhs, rhs)
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    if (value == null) {
      Restrictions.isNull(property)
    } else {
      Restrictions.isNotNull(property)
    }
  }
  
  String toString() {
    return "(${lhsValue} == ${rhsValue})"
  }
  
}