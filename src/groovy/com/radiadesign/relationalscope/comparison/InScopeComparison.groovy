package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.RelationalScope

class InScopeComparison extends ScopeComparisonBase {
  
  InScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion(options) {
    def property = fullPropertyNameFor(options, propertyName)
    if (comparisonValue instanceof RelationalScope) {
      return Subqueries.propertyIn( property, detachedCriteriaFor(comparisonValue, options) )
    } else {
      return Restrictions.in(property, comparisonValue)
    }
  }
  
  String toString() {
    return "(${propertyName} in ${comparisonValue})"
  }
  
}