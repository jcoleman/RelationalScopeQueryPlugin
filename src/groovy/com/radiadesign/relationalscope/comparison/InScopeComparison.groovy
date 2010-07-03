package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.RelationalScope
import com.radiadesign.relationalscope.expression.*

class InScopeComparison extends ScopeComparisonBase {
  
  InScopeComparison(_lhsValue, _rhsValue) {
    super(_lhsValue, _rhsValue)
  }
  
  def detachedCriteriaCallback(RelationalScope scope, criteria) {
    if (!scope.selections) {
      //criteria.setProjection(Projections.id())
    }
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    Restrictions.in(property, value)
  }
  
  Criterion criterionForPropertyAndSubquery(property, criteria, options) {
    Subqueries.propertyIn(property, criteria)
  }
  
  String toString() {
    return "(${lhsValue} in ${rhsValue})"
  }
  
}