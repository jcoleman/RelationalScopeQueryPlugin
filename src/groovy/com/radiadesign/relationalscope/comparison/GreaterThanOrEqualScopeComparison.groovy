package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*
import com.radiadesign.hibernate.criterion.ArbitraryExpressionCriterion

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
  
  Criterion criterionForValueAndProperty(value, property, options) {
    Restrictions.le(property, value)
  }
    
  Criterion criterionForPropertyAndSubquery(property, criteria, options) {
    Subqueries.propertyGe(property, criteria)
  }
  
  Criterion criterionForValueAndSubquery(value, criteria, options) {
    Subqueries.ge(value, criteria)
  }
  
  Criterion  criterionForExpressionAndExpression(lhs, rhs, options) {
    new ArbitraryExpressionCriterion(lhs, rhs, '>=', options)
  }
  
  String toString() {
    return "(${lhsValue} >= ${rhsValue})"
  }
  
  static operatorInspectString = 'gte'
  
}