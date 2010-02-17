package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class EqualsScopeComparison extends ScopeComparisonBase {
  
  EqualsScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion() {
    return Restrictions.eq(propertyName, comparisonValue)
  }
  
  String toString() {
    return "(${propertyName} == ${comparisonValue})"
  }
  
}