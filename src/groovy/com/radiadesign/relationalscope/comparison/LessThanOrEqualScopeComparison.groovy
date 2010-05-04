package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class LessThanOrEqualScopeComparison extends ScopeComparisonBase {
  
  LessThanOrEqualScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion(criteria, associationPath, associationAliases) {
    def property = fullPropertyNameFor(associationAliases, associationPath, propertyName)
    return Restrictions.le(property, comparisonValue)
  }
  
  String toString() {
    return "(${propertyName} <= ${comparisonValue})"
  }
  
}