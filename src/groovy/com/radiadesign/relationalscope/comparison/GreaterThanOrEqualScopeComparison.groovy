package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class GreaterThanOrEqualScopeComparison extends ScopeComparisonBase {
  
  GreaterThanOrEqualScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion(criteria, associationPath, associationAliases) {
    def property = fullPropertyNameFor(associationAliases, associationPath, propertyName)
    return Restrictions.ge(property, comparisonValue)
  }
  
  String toString() {
    return "(${propertyName} >= ${comparisonValue})"
  }
  
}