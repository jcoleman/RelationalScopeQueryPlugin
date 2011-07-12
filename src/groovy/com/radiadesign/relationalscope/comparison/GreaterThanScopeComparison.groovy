package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*
import com.radiadesign.hibernate.criterion.ArbitraryExpressionCriterion

class GreaterThanScopeComparison extends ScopeComparisonBase {
  
  GreaterThanScopeComparison(_lhsValue, _rhsValue) {
    super(_lhsValue, _rhsValue)
  }
  
  Criterion criterionForPropertyAndProperty(lhs, rhs, options) {
    Restrictions.gtProperty(lhs, rhs)
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    Restrictions.gt(property, value)
  }
  
  Criterion criterionForValueAndProperty(value, property, options) {
    Restrictions.lt(property, value)
  }
  
  Criterion criterionForPropertyAndSubquery(property, criteria, options) {
    Subqueries.propertyGt(property, criteria)
  }

  Criterion criterionForValueAndSubquery(value, criteria, options) {
    Subqueries.gt(value, criteria)
  }
  
  Criterion  criterionForExpressionAndExpression(lhs, rhs, options) {
    new ArbitraryExpressionCriterion(lhs, rhs, '>', options)
  }
  
  String toString() {
    return "(${lhsValue} > ${rhsValue})"
  }
  
  static operatorInspectString = 'gt'
  
}