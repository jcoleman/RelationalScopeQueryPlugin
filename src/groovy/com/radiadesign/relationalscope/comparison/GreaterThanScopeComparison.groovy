package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class GreaterThanScopeComparison extends ScopeComparisonBase {
  
  GreaterThanScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion(criteria, associationPath, associationAliases) {
    def property = fullPropertyNameFor(associationAliases, associationPath, propertyName)
    return Restrictions.gt(property, comparisonValue)
  }
  
  String toString() {
    return "(${propertyName} > ${comparisonValue})"
  }
  
}