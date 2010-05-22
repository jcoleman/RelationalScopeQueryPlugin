package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class LessThanScopeComparison extends ScopeComparisonBase {
  
  LessThanScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion(options) {
    def property = fullPropertyNameFor(options, propertyName)
    return Restrictions.lt(property, comparisonValue)
  }
  
  String toString() {
    return "(${propertyName} < ${comparisonValue})"
  }
  
}