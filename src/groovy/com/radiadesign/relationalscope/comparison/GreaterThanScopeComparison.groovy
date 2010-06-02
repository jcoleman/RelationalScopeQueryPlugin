package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*

class GreaterThanScopeComparison extends ScopeComparisonBase {
  
  GreaterThanScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion(options) {
    def property = LocalPropertyExpression.fullPropertyNameFor(options, propertyName)
    return Restrictions.gt(property, comparisonValue)
  }
  
  String toString() {
    return "(${propertyName} > ${comparisonValue})"
  }
  
}