package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class EqualsScopeComparison extends ScopeComparisonBase {
  
  EqualsScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion(options) {
    def property = fullPropertyNameFor(options, propertyName)
    return Restrictions.eq(property, comparisonValue)
  }
  
  String toString() {
    return "(${propertyName} == ${comparisonValue})"
  }
  
}