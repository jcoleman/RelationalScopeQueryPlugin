package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class GreaterThanOrEqualScopeComparison extends ScopeComparisonBase {
  
  GreaterThanOrEqualScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion() {
    return Restrictions.ge(propertyName, comparisonValue)
  }
  
  String toString() {
    return "(${propertyName} >= ${comparisonValue})"
  }
  
}