package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*

class GreaterThanOrEqualScopeComparison extends ScopeComparisonBase {
  
  GreaterThanOrEqualScopeComparison(_lhsValue, _rhsValue) {
    super(_lhsValue, _rhsValue)
  }
  
  Criterion criterionForPropertyAndProperty(lhs, rhs, options) {
    Restrictions.geProperty(lhs, rhs)
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    Restrictions.ge(property, value)
  }
    
  Criterion criterionForPropertyAndSubquery(property, criteria, options) {
    Subqueries.propertyGe(property, criteria)
  }
  
  Criterion criterionForValueAndSubquery(value, criteria, options) {
    Subqueries.ge(value, criteria)
  }
  
  String toString() {
    return "(${lhsValue} >= ${rhsValue})"
  }
  
}