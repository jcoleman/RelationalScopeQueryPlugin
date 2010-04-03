package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class LessThanOrEqualScopeComparison extends ScopeComparisonBase {
  
  LessThanOrEqualScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion() {
    return Restrictions.le(propertyName, comparisonValue)
  }
  
  String toString() {
    return "(${propertyName} <= ${comparisonValue})"
  }
  
}