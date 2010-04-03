package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class GreaterThanScopeComparison extends ScopeComparisonBase {
  
  GreaterThanScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion() {
    return Restrictions.gt(propertyName, comparisonValue)
  }
  
  String toString() {
    return "(${propertyName} > ${comparisonValue})"
  }
  
}