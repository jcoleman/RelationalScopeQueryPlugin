package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*
import com.radiadesign.hibernate.criterion.ArbitraryExpressionCriterion

class LessThanOrEqualScopeComparison extends ScopeComparisonBase {
  
  LessThanOrEqualScopeComparison(_lhsValue, _rhsValue) {
    super(_lhsValue, _rhsValue)
  }
  
  Criterion criterionForPropertyAndProperty(lhs, rhs, options) {
    Restrictions.leProperty(lhs, rhs)
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    Restrictions.le(property, value)
  }
  
  Criterion criterionForValueAndProperty(value, property, options) {
    Restrictions.ge(property, value)
  }
  
  Criterion criterionForPropertyAndSubquery(property, criteria, options) {
    Subqueries.propertyLe(property, criteria)
  }
  
  Criterion criterionForValueAndSubquery(value, criteria, options) {
    Subqueries.le(value, criteria)
  }
  
  Criterion  criterionForExpressionAndExpression(lhs, rhs, options) {
    new ArbitraryExpressionCriterion(lhs, rhs, '<=', options)
  }
  
  String toString() {
    return "(${lhsValue} <= ${rhsValue})"
  }
  
}