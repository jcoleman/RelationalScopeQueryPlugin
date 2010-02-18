package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class IsScopeComparison extends ScopeComparisonBase {
  
  IsScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion() {
    if (comparisonValue == null) {
      return Restrictions.isNull(propertyName)
    } else {
      return Restrictions.isNotNull(propertyName)
    }
  }
  
  String toString() {
    return "(${propertyName} == ${comparisonValue})"
  }
  
}