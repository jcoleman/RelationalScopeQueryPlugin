package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class IsScopeComparison extends ScopeComparisonBase {
  
  IsScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion(criteria, associationPath, associationAliases) {
    def property = fullPropertyNameFor(associationAliases, associationPath, propertyName)
    if (comparisonValue == null) {
      return Restrictions.isNull(property)
    } else {
      return Restrictions.isNotNull(property)
    }
  }
  
  String toString() {
    return "(${propertyName} == ${comparisonValue})"
  }
  
}