package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*
import com.radiadesign.hibernate.criterion.ArbitraryExpressionCriterion

class LessThanScopeComparison extends ScopeComparisonBase {
  
  LessThanScopeComparison(_lhsValue, _rhsValue) {
    super(_lhsValue, _rhsValue)
  }
  
  Criterion criterionForPropertyAndProperty(lhs, rhs, options) {
    Restrictions.ltProperty(lhs, rhs)
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    Restrictions.lt(property, value)
  }
  
  Criterion criterionForValueAndProperty(value, property, options) {
    Restrictions.gt(property, value)
  }
  
  Criterion criterionForPropertyAndSubquery(property, criteria, options) {
    Subqueries.propertyLt(property, criteria)
  }
  
  Criterion criterionForValueAndSubquery(value, criteria, options) {
    Subqueries.lt(value, criteria)
  }
  
  Criterion  criterionForExpressionAndExpression(lhs, rhs, options) {
    new ArbitraryExpressionCriterion(lhs, rhs, '<', options)
  }
  
  String toString() {
    return "(${lhsValue} < ${rhsValue})"
  }
  
  static operatorInspectString = 'lt'
  
}