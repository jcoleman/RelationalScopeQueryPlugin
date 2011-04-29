package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.RelationalScope
import com.radiadesign.relationalscope.expression.*
import com.radiadesign.hibernate.criterion.ArbitraryExpressionCriterion

class InScopeComparison extends ScopeComparisonBase {
  
  InScopeComparison(_lhsValue, _rhsValue) {
    super(_lhsValue, _rhsValue)
  }
  
  def detachedCriteriaCallback(RelationalScope scope, criteria) {
    if (!scope.selections) {
      criteria.setProjection(Projections.id())
    }
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    Restrictions.in(property, value)
  }
  
  Criterion criterionForPropertyAndSubquery(property, criteria, options) {
    Subqueries.propertyIn(property, criteria)
  }
  
  Criterion  criterionForExpressionAndExpression(lhs, list, options) {
    new ArbitraryExpressionCriterion(lhs, list, 'IN', options)
  }
  
  String toString() {
    return "(${lhsValue} in ${rhsValue})"
  }
  
}