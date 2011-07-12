package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*
import com.radiadesign.hibernate.criterion.ArbitraryExpressionCriterion

class BetweenScopeComparison extends ScopeComparisonBase {
  
  BetweenScopeComparison(_lhsValue, _rhsValue) {
    super(_lhsValue, _rhsValue)
  }
  
  Criterion criterionForPropertyAndValue(property, list, options) {
    Restrictions.between(property, list[0], list[1])
  }
  
  Criterion  criterionForExpressionAndExpression(lhs, list, options) {
    new ArbitraryExpressionCriterion(lhs, list, 'BETWEEN', options, true)
  }
  
  String toString() {
    return "(${lhsValue} between ${rhsValue.join(' and ')})"
  }
  
  static operatorInspectString = 'between'
  
}