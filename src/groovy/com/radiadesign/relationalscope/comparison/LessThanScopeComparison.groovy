package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*

class LessThanScopeComparison extends ScopeComparisonBase {
  
  LessThanScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion(options) {
    def property = LocalPropertyExpression.aliasedPropertyNameFor(options, propertyName)
    if (comparisonValue instanceof LocalPropertyExpression) {
      return Restrictions.ltProperty( property, comparisonValue.propertyFor(options) )
    } else {
      return Restrictions.lt(property, comparisonValue)
    }
  }
  
  String toString() {
    return "(${propertyName} < ${comparisonValue})"
  }
  
}